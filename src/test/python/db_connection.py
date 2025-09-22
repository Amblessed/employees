import os
from psycopg2 import pool
from psycopg2.extras import DictCursor

# Initialize connection pool
# Use a connection pool instead of reconnecting on every call. psycopg2.pool.SimpleConnectionPool handles this.
db_pool = pool.SimpleConnectionPool(
    minconn=1,
    maxconn=5,
    dbname="employeeDB",
    user=os.getenv("POSTGRESQL_USERNAME"),
    password=os.getenv("POSTGRESQL_PASSWORD"),
    host="localhost",
    port="5432",
)


def _fetch(query: str, params: tuple = None, fetchone: bool = False):
    conn = db_pool.getconn()
    try:
        with conn.cursor(cursor_factory=DictCursor) as cursor:
            cursor.execute(query, params or ())
            return cursor.fetchone() if fetchone else cursor.fetchall()
    finally:
        db_pool.putconn(conn)


def normalize_keys(d: dict) -> dict:
    """
    Convert lowercase keys to camelCase equivalents if needed.
    """
    key_map = {
        "firstname": "firstName",
        "lastname": "lastName",
        "email": "email"  # keep as-is
    }
    return {key_map.get(k, k): v for k, v in d.items()}



def get_employee_from_db(employee_id: str):
    row = _fetch(
        "SELECT first_name AS firstName, last_name AS lastName, email "
        "FROM employees WHERE user_id = %s",
        (employee_id,),
        fetchone=True,
    )
    return normalize_keys(dict(row)) if row else None


def get_all_employees_from_db():
    rows = _fetch("SELECT first_name AS firstName, last_name AS lastName, email FROM employees")
    return [dict(r) for r in rows]


def get_all_employees_search_from_db(department: str, position: str, salary: int):
    rows = _fetch(
        "SELECT department, position, salary FROM employees "
        "WHERE department = %s AND position = %s AND salary >= %s",
        (department, position, salary),
    )
    return [dict(r) for r in rows]


def get_all_emp_ids_from_db():
    rows = _fetch("SELECT user_id AS id FROM employees")
    return [r["id"] for r in rows]


if __name__ == "__main__":
    print(get_all_emp_ids_from_db())
