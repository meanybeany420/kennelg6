# KennelG6
## Kennel Reservation program
This project is a kennel reservation management system designed for kennel staff to manage pet booking efficiently.
The application allows staff to :
- Manage cat and dog reservations
- View current and future bookings
- Prevent kennel overbooking
- Store customer and pet information
- Allocate kennel based on animal size
- Track kennel availability 

## Features
- Add bookings
- Remove booking
- View reservation
- Prevent overcapacity booking
- Dog size allocation system
- Databased storage 
- User friendly graphical interface
***


## Run locally

Requires JDK 17 or newer and Bash. From the repository root:

```sh
bash run.sh
```

The application creates a local SQLite database and initial kennel configuration on first launch. Customer, pet and booking records are not included in this repository.

## Project credit

University group coursework. Original Git history credits Owen Hynes, Aishwarya Lakshmi Krishnan, Anthony Acheampong and Samir Dimachk. This portfolio copy preserves the current application source without publishing university repository metadata or runtime databases. It does not imply sole authorship.

SQLite JDBC is a third-party dependency bundled in `kennelapplication/lib/`.
