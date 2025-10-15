from .base import Base

class Production(Base):
    """
    Production settings.
    """
    DEBUG = False
    ALLOWED_HOSTS = []