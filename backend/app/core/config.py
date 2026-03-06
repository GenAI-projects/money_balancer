from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    database_url: str = "sqlite:///./money_balancer.db"
    app_mode: str = "development"

    model_config = SettingsConfigDict(env_prefix="", case_sensitive=False)


settings = Settings()
