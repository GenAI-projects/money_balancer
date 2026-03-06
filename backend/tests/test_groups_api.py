def test_create_group_and_add_members(client):
    create = client.post('/api/groups', json={'name': 'Roommates', 'base_currency': 'usd'})
    assert create.status_code == 201
    group = create.json()
    assert group['name'] == 'Roommates'
    assert group['base_currency'] == 'USD'

    group_id = group['id']
    add1 = client.post(f'/api/groups/{group_id}/members', json={'name': 'Alex'})
    assert add1.status_code == 200
    assert len(add1.json()['members']) == 1

    add2 = client.post(f'/api/groups/{group_id}/members', json={'name': 'Rina'})
    assert add2.status_code == 200
    members = add2.json()['members']
    assert len(members) == 2
    assert {m['user']['name'] for m in members} == {'Alex', 'Rina'}


def test_get_group_not_found(client):
    response = client.get('/api/groups/999')
    assert response.status_code == 404
