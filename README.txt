{\rtf1\ansi\ansicpg1252\cocoartf1561\cocoasubrtf400
{\fonttbl\f0\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;}
{\*\expandedcolortbl;;}
\margl1440\margr1440\vieww14020\viewh8400\viewkind0
\pard\tx720\tx1440\tx2160\tx2880\tx3600\tx4320\tx5040\tx5760\tx6480\tx7200\tx7920\tx8640\pardirnatural\partightenfactor0

\f0\fs24 \cf0 Author: Garren Steigers\
App: CityGuide\
\
Third Party APIs:\
- Google Places API\
	- I\'92m using the Web API integration, because I found that I was better able to handle searches\
	and location parsing with more fidelity than I could through just the android integration\
	- {\field{\*\fldinst{HYPERLINK "https://developers.google.com/places/web-service/search"}}{\fldrslt https://developers.google.com/places/web-service/search}}\
- Google Maps\
	- For the Details page I was able to integrate the SupportMapFragment to render the maps and \
	to pinpoint the location via the selected location\'92s address\
\
Features:\
- CustomSpinner\
	- I was able to utilize the default Android SeekBar and customize the progress and marker to\
	get the look and feel of the Slider control.\
	- I then simply overlaid textviews over the SeekBar to create the menu system.\
	- Moving forward, I would look at taking that and creating a custom view to put that all together\
	with the TextViews and SeekBar to create one way to manage it.\
\
}