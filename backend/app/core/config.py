from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    app_name: str = "FreeWise API"
    database_url: str = "postgresql+psycopg://postgres:postgres@localhost:5432/freewise"

    model_config = SettingsConfigDict(env_file=".env", env_prefix="FREEWISE_")


settings = Settings()
