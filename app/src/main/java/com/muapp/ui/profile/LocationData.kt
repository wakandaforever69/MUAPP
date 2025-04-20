package com.muapp.ui.profile

object LocationData {
    // List of countries
    val countries = listOf(
        "India",
        "United States",
        "United Kingdom",
        "Canada",
        "Australia",
        "Germany",
        "France",
        "Japan",
        "China",
        "Singapore"
    )

    // Map of states by country
    val statesByCountry = mapOf(
        "India" to listOf(
            "Andhra Pradesh",
            "Arunachal Pradesh",
            "Assam",
            "Bihar",
            "Chhattisgarh",
            "Goa",
            "Gujarat",
            "Haryana",
            "Himachal Pradesh",
            "Jharkhand",
            "Karnataka",
            "Kerala",
            "Madhya Pradesh",
            "Maharashtra",
            "Manipur",
            "Meghalaya",
            "Mizoram",
            "Nagaland",
            "Odisha",
            "Punjab",
            "Rajasthan",
            "Sikkim",
            "Tamil Nadu",
            "Telangana",
            "Tripura",
            "Uttar Pradesh",
            "Uttarakhand",
            "West Bengal",
            "Delhi",
            "Jammu and Kashmir"
        ),
        "United States" to listOf(
            "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware",
            "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky",
            "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi",
            "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico",
            "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania",
            "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
            "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
        ),
        "United Kingdom" to listOf(
            "England", "Scotland", "Wales", "Northern Ireland"
        ),
        "Canada" to listOf(
            "Alberta", "British Columbia", "Manitoba", "New Brunswick", "Newfoundland and Labrador",
            "Northwest Territories", "Nova Scotia", "Nunavut", "Ontario", "Prince Edward Island",
            "Quebec", "Saskatchewan", "Yukon"
        ),
        "Australia" to listOf(
            "New South Wales", "Queensland", "South Australia", "Tasmania", "Victoria", "Western Australia",
            "Australian Capital Territory", "Northern Territory"
        )
        // More countries can be added as needed
    )

    // Map of cities by state (using country-state as key)
    val citiesByState = mapOf(
        // India - Maharashtra
        "India-Maharashtra" to listOf(
            "Mumbai", "Pune", "Nagpur", "Thane", "Nashik", "Aurangabad", "Solapur", "Kolhapur", "Amravati", "Navi Mumbai"
        ),
        // India - Karnataka
        "India-Karnataka" to listOf(
            "Bengaluru", "Mysuru", "Hubballi-Dharwad", "Mangaluru", "Belagavi", "Kalaburagi", "Davanagere", "Ballari", "Vijayapura", "Shivamogga"
        ),
        // India - Tamil Nadu
        "India-Tamil Nadu" to listOf(
            "Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem", "Tirunelveli", "Tiruppur", "Vellore", "Erode", "Thoothukkudi"
        ),
        // US - California
        "United States-California" to listOf(
            "Los Angeles", "San Diego", "San Francisco", "San Jose", "Fresno", "Sacramento", "Long Beach", "Oakland", "Bakersfield", "Anaheim"
        ),
        // US - New York
        "United States-New York" to listOf(
            "New York City", "Buffalo", "Rochester", "Yonkers", "Syracuse", "Albany", "New Rochelle", "Mount Vernon", "Schenectady", "Utica"
        ),
        // UK - England
        "United Kingdom-England" to listOf(
            "London", "Birmingham", "Manchester", "Leeds", "Newcastle", "Liverpool", "Bristol", "Sheffield", "Nottingham", "Southampton"
        )
        // More states can be added as needed
    )
}