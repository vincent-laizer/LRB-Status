""" deals with all database access required """
import sqlite3 as sql

def init_db():
    """ function to create db """
    conn = sql.connect('database.db')
    print("Opened database successfully")

    # create requests tabel
    conn.execute('CREATE TABLE request(id INTEGER PRIMARY KEY, ip TEXT, date DATE)')
    print("Requests Table created successfully")

    conn.execute('CREATE TABLE feedback(id INTEGER PRIMARY KEY, message TEXT, date DATE)')
    conn.close()

def store_request(ip:str, date:str):
    """ save requests to db """
    try:
        with sql.connect("database.db") as con:
            cur = con.cursor()
            cur.execute("INSERT INTO request (ip,date) VALUES (?,?)",(ip,date))
        con.commit()
        msg = True
    except:
        con.rollback()
        msg = False
    finally:
        con.close()
        return msg

def store_feedback(feedback:str, date:str):
    """ save feedback to db """
    try:
        with sql.connect("database.db") as con:
            cur = con.cursor()
            cur.execute("INSERT INTO feedback (message,date) VALUES (?,?)",(feedback,date))
        con.commit()
        msg = True
    except:
        con.rollback()
        msg = False
    finally:
        con.close()
        return msg

def fetch_feedback():
    """ return all the feedback from the db """
    with sql.connect('database.db') as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT * FROM feedback")
        data = cursor.fetchall()
        orders = []
        for d in data:
            orders.append({
                "id": d[0],
                "message": d[1],
                "date": d[2]
                })
        return {"feedbacks": orders}

def fetch_requests_all():
    with sql.connect('database.db') as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT * FROM request")
        data = cursor.fetchall()
        orders = []
        for d in data:
            orders.append({
                "id": d[0],
                "ip": d[1],
                "date": d[2]
                })
        return {"requests": orders}

def fetch_requests(n:int):
    with sql.connect('database.db') as conn:
        cursor = conn.cursor()
        cursor.execute(f"SELECT * FROM request ORDER BY id DESC LIMIT {n}")
        data = cursor.fetchall()
        orders = []
        for d in data:
            orders.append({
                "id": d[0],
                "ip": d[1],
                "date": d[2]
                })
        return {"requests": orders}

def fetch_requests_time(time:str):
    with sql.connect('database.db') as conn:
        cursor = conn.cursor()
        cursor.execute(f"SELECT * FROM request WHERE date > '{time}'")
        data = cursor.fetchall()
        orders = []
        for d in data:
            orders.append({
                "id": d[0],
                "ip": d[1],
                "date": d[2]
                })
        return {"requests": orders}
