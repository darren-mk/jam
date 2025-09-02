import os
import sqlalchemy as sa
from sqlalchemy.orm import declarative_base, Mapped, mapped_column, relationship, sessionmaker

DB_URL = sa.URL.create(
    "postgresql+psycopg",
    username=os.environ["PGUSER"],
    password=os.environ["PGPASSWORD"],
    host=os.environ["PGHOST"],
    port=int(os.environ["PGPORT"]),
    database=os.environ["PGDATABASE"])

engine = sa.create_engine(
    DB_URL, echo=True, future=True)

Session = sessionmaker(
    bind=engine, future=True)

Base = declarative_base()

class Member(Base):
    __tablename__ = "member"
    id: Mapped[int] = mapped_column(primary_key=True)
    first_name: Mapped[str | None] = mapped_column(sa.String)
    last_name: Mapped[str | None] = mapped_column(sa.String)
    email: Mapped[str | None] = mapped_column(sa.String, unique=True)
    addresses: Mapped[list["Address"]] = relationship(back_populates="member")
    def __repr__(self):
        return f"Member(id={self.id}, first_name={self.first_name}, email={self.email})"

class Address(Base):
    __tablename__ = "address"
    id: Mapped[int] = mapped_column(primary_key=True)
    city: Mapped[str] = mapped_column(sa.String(100), nullable=False)
    member_id: Mapped[int | None] = mapped_column(sa.ForeignKey("member.id"))
    member: Mapped["Member"] = relationship(back_populates="addresses")

def add_member(first_name, last_name, email):
    with Session() as session:
        m = Member(
            first_name=first_name,
            last_name=last_name,
            email=email)
        session.add(m)
        session.commit()

def add_address(city, member_id):
    with Session() as session:
        m = Address(
            city = city,
            member_id = member_id)
        session.add(m)
        session.commit()

def get_all_members():
    with Session() as session:
        return session.query(Member).all()

if __name__ == '__main__':
    Base.metadata.create_all(engine)
    # add_member('Alice', 'Maine', 'alicemaine@eml.com')
    add_address('Santorini', 1)
    members = get_all_members()
    for member in members:
        print(member)
