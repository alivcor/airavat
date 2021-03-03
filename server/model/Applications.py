import sqlalchemy as db


class Applications:
    def __init__(self, session):
        self.session = session
        self.applications = db.Table('AIRAVAT_APPLICATIONS',
                                session.dbEngine.metadata,
                                autoload=True,
                                autoload_with=session.dbEngine.engine)

    def fetchAll(self):
        apps = db.select([self.applications])
        resultSet = self.session.dbEngine.db_session.query(apps)
        return [r._asdict() for r in resultSet]
        # return resultSet.__dict__
