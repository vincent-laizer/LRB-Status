#include <iostream>
#include <winsock2.h>
#include <cstdio>
#include <cstdlib>
#include <curl/curl.h>
#include <windows.h>

using namespace std;

// using winsock to get IP address of computer
string getIP(int, char **)
{
	string address = "1";
    char ac[80];
    if (gethostname(ac, sizeof(ac)) == SOCKET_ERROR) {
        cerr << "Error " << WSAGetLastError() << " when getting local host name." << endl;
        return "1";
    }

    struct hostent *phe = gethostbyname(ac);
    if (phe == 0) {
        cerr << "Yow! Bad host lookup." << endl;
        return "1";
    }
    
    string one, two, three, four, five, six, seven;
    for (int i = 0; phe->h_addr_list[i] != 0; ++i) {
        struct in_addr addr;
        memcpy(&addr, phe->h_addr_list[i], sizeof(struct in_addr));
        string temp = inet_ntoa(addr);
        one = temp[0];
		two = temp[1];
		three = temp[2];
		four = temp[3];
		five = temp[4];
		six = temp[5];
		seven = temp[6];
		
		string complete = one+two+three+four+five+six+seven;
		if(complete == "172.16."){
			// the ip address is in UDOM network, return it
			address = temp;
//			cout<<"UDOM Network"<<endl; no need, the app runs in the background
		}
    }
    
    return address;
}

// functions to send get request to server using curl
size_t WriteCallback(char *contents, size_t size, size_t nmemb, void *userp){
	((string *)userp)->append((char *)contents, size * nmemb);
	return size * nmemb;
}

string getJSON(string URL){
	CURL *curl;
	CURLcode res;
	string readBuffer;

	curl = curl_easy_init();
	if (curl){
		curl_easy_setopt(curl, CURLOPT_HEADER, 1);
		curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, false);
		curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, true);
		curl_easy_setopt(curl, CURLOPT_HEADER, false);
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
		curl_easy_setopt(curl, CURLOPT_URL, URL.c_str());
		curl_easy_setopt(curl, CURLOPT_WRITEDATA, &readBuffer);

		res = curl_easy_perform(curl);

		/* always cleanup */
		curl_easy_cleanup(curl);
		return readBuffer;
	}
	return 0;
}

// add registry key to make program run on startup of system
// toogle 0-off, 1-on
void configure(int toogle){
	// not needed but i just wanted it to be here for future ref, bad coding practice but F it.
	HKEY profile = HKEY_CURRENT_USER;
	char re[MAX_PATH];
	string FP = string(re, GetModuleFileName(NULL, re, MAX_PATH));
	LONG ln = RegGetValue(profile, "SOFTWARE\\Microsoft\\Windows\\Currentversion\\Run", "LRBStatus",
				RRF_RT_REG_SZ, 0, 0, 0);
	// ends here, unneccessary code
	
	if(toogle == 1){
		// enable start on system startup
		HKEY hkey;
		LONG key = RegOpenKeyEx(profile, "SOFTWARE\\Microsoft\\Windows\\Currentversion\\Run",
					0, KEY_WRITE, &hkey);
		if (ERROR_SUCCESS == key){
			key = RegSetValueEx(hkey, "LRBStatus", 0, REG_SZ, (BYTE*)FP.c_str(), strlen(FP.c_str()));
		}
	}
	else{
		// disable start on system startup
		HKEY hkey = HKEY_CURRENT_USER;
		RegOpenKey(profile, "SOFTWARE\\Microsoft\\Windows\\Currentversion\\Run", &hkey);
		RegDeleteValue(hkey, "LRBStatus");
		RegCloseKey(hkey);
	}
}

// main method where the magic happens
int main(int argc, char *argv[]){
	// go to background
	FreeConsole();

	// configure to run on startup
	configure(1);

	// enter a forever loop
	while(1){
		// get ip address
		 WSAData wsaData;
	    if (WSAStartup(MAKEWORD(1, 1), &wsaData) != 0) {
	        return 255;
	    }
	
	    string ip = getIP(argc, argv);
//	    cout<<ip<<endl; no need, the app runs in the background
	    if(ip != "1"){
	    	// it has successfully found UDOM network ip address
	    	//send request to server
	    	string response = getJSON("https://boomboomboom.pythonanywhere.com/provide/"+ip);
//	    	cout<<response<<endl; no need, the app runs in the background
		}
	
	    WSACleanup();
	    
	    // take a short break, to avoid overusing pc and server resources
	    // waiting for 15 minutes
	    int minutes = 15;
	    Sleep(1000*60*minutes);
	}

    return 0;
}
