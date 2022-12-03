import sqlite3 as sql
""" Docstring for this module """

import datetime
from flask import Flask, jsonify, request
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

# ---- methods initialy found in db.py ----

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

# ---- end of db.py methods ----

def storeRequest(ip:str):
    """ store the request in db """
    store_request(ip, datetime.datetime.now())

def networkStatus():
    """ fetch the details and perform neccessary computations """
    no_hours = 1 # number of hours ago to count and include the requests issued
    avg_people = 1 # average number of people sending requests per hour
    current_time = datetime.datetime.now()
    check_time = current_time - datetime.timedelta(hours=no_hours)

    msgs = ["Internet Unavailable", "Not Sure", "Internet Available"]

    results = fetch_requests_time(f"{check_time:%Y-%m-%d %H:%M:%S}")
    no_requests = len(results["requests"])

    if no_requests > 0:
        print(no_requests)
        waiting_time = 3 #in minutes, that a single provider waits before sending another request

        percent = waiting_time*no_requests*10/(6*no_hours*avg_people)
        if percent > 50.0:
            index = 2
        elif percent > 30.0:
            index = 1
        else:
            index = 0

        last_checked = results['requests'][no_requests-1]['date']
        last_checked = datetime.datetime.strptime(last_checked, '%Y-%m-%d %H:%M:%S.%f')
        # account for the 3 hour time difference between pythonanywhere servers and local time
        last_checked = last_checked + datetime.timedelta(hours=3)
        status = {
            "last_checked": f"{last_checked:%d/%m/%Y %H:%M:%S}",
            "confidence": f"{percent:.01f}",
            "status": msgs[index],
            "icon_value": index
        }
        return status

    most_current = fetch_requests(1)['requests'][0]['date'] # most current request
    most_current = datetime.datetime.strptime(most_current, '%Y-%m-%d %H:%M:%S.%f')
    # account for the 3 hour time difference between pythonanywhere servers and local time
    most_current = most_current + datetime.timedelta(hours=3)
    status = {
        "last_checked": f"{most_current:%d/%m/%Y %H:%M:%S}",
        "confidence": "0.0",
        "status": msgs[0],
        "icon_value": 0
    }
    return status

@app.route("/")
def home():
    """ list of all the requests made so far"""
    return jsonify(fetch_requests_all())

@app.route("/consume")
def consume():
    """ use the data collected, accessed by mobile app """
    return jsonify(networkStatus())

@app.route("/provide/<string:ip>")
def provide(ip):
    """ method requested by provider(desktop app) only """
    value = ip.split(".")
    gateway = f"{value[0]}.{value[1]}.{value[2]}.1"
    udom_gateway = "172.16.56.1" #thats for lrb 106, 172.16.55.1 for lrb001
    print(gateway)

    # if gateway == udom_gateway:
        # storeRequest(ip)
        # return f"your ip is {ip}, you are in UDOM network"
    storeRequest(ip)
    return f"your ip is {ip}"

@app.route("/feedback", methods=["GET","POST"])
def feedback():
    """ store or retrive feedback from user """
    if request.method == "POST":
        feedback = request.args["feedback"]
        store_feedback(feedback, datetime.datetime.now())
        return {"response":"thank you!"}
    else:
        return jsonify(fetch_feedback())

@app.route("/config")
def config():
    """ configure the db for first time use """
    print("Configuring ..")
    init_db()
    print("DB Configured")
    return "sucess"

if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)

