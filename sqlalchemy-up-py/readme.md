# sqlalchemy-up-py

## bring container for dependencies:
```shell
podman-compose up
```

## set up python env:
```shell
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
export PGUSER=local_user
export PGPASSWORD=local_pass
export PGHOST=localhost
export PGPORT=5432
export PGDATABASE=sqlalchemy_up_py_main_db
```

