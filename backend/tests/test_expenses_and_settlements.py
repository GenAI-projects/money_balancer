def seed_group(client):
    group = client.post('/api/groups', json={'name': 'Trip', 'base_currency': 'USD'}).json()
    gid = group['id']
    first = client.post(f'/api/groups/{gid}/members', json={'name': 'Alex'}).json()
    second = client.post(f'/api/groups/{gid}/members', json={'name': 'Rina'}).json()
    users = [m['user']['id'] for m in second['members']]
    return gid, users


def test_create_expense_and_balances(client):
    gid, users = seed_group(client)
    alex, rina = users

    payload = {
        'title': 'Dinner',
        'amount': '60.00',
        'currency': 'USD',
        'paid_by_user_id': alex,
        'split_user_ids': [alex, rina],
        'category': 'FOOD',
        'note': 'Sushi',
    }
    created = client.post(f'/api/groups/{gid}/expenses', json=payload)
    assert created.status_code == 201
    body = created.json()
    assert body['title'] == 'Dinner'
    assert len(body['splits']) == 2
    assert body['splits'][0]['share_amount'] == '30.00'

    balances = client.get(f'/api/groups/{gid}/settlements/balances')
    assert balances.status_code == 200
    bal = {x['user_name']: x['balance'] for x in balances.json()}
    assert bal['Alex'] == '30.00'
    assert bal['Rina'] == '-30.00'

    settlements = client.get(f'/api/groups/{gid}/settlements')
    assert settlements.status_code == 200
    rows = settlements.json()
    assert len(rows) == 1
    assert rows[0]['from_user_name'] == 'Rina'
    assert rows[0]['to_user_name'] == 'Alex'
    assert rows[0]['amount'] == '30.00'


def test_expense_rejects_non_member_split(client):
    gid, users = seed_group(client)
    alex, rina = users

    payload = {
        'title': 'Cab',
        'amount': '30.00',
        'currency': 'USD',
        'paid_by_user_id': alex,
        'split_user_ids': [alex, rina, 999],
        'category': 'TRANSPORT',
    }
    response = client.post(f'/api/groups/{gid}/expenses', json=payload)
    assert response.status_code == 400
