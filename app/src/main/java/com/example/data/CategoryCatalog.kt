package com.example.data

data class ToolCategory(
    val id: Int,
    val title: String,
    val iconName: String,
    val description: String,
    val toolCount: Int
)

data class ToolItem(
    val id: Int,
    val categoryId: Int,
    val name: String,
    val description: String,
    val isHardwareDependent: Boolean = false,
    val fallbackInfo: String = ""
)

object CategoryCatalog {
    val categories = listOf(
        ToolCategory(1, "Light & Signaling", "FlashlightOn", "Flashlights, SOS Morse, Strobe, Emergency Beacons", 12),
        ToolCategory(2, "Audio & Alerts", "VolumeUp", "Sirens, Whistle, Dog Deterrent, Audio Memos, Ultrasonic", 13),
        ToolCategory(3, "Radio & Broadcast", "Radio", "FM/AM Radio info, NOAA channels, Scanners, Walkie-Talkie", 10),
        ToolCategory(4, "Navigation & Sensors", "Explore", "Compass, GPS, Waypoints, Barometer, Astronomy, Stars", 13),
        ToolCategory(5, "Maps & Offline Data", "Map", "Offline Maps, Pin Markers, Distance/Area, Coordinate Converter", 8),
        ToolCategory(6, "Knowledge & Manuals", "MenuBook", "First-Aid, CPR, Water, Fire, Shelter, Knots, Foraging", 12),
        ToolCategory(7, "Health & Safety", "MedicalServices", "Checklist, CPR Metronome, Medical Log, Contacts, Blood Card", 10),
        ToolCategory(8, "Power & Battery", "BatteryChargingFull", "Battery Monitor, Saver, Solar Log, Time Remaining", 8),
        ToolCategory(9, "Survival Utilities", "Build", "Ruler, Level, Magnifier, Stopwatch, Clock, Calculator, Notes", 12),
        ToolCategory(10, "Emergency & SOS", "Warning", "One-Tap SOS, Panic Button, Emergency Numbers, Panic Wipe", 10),
        ToolCategory(11, "Extras & QoL", "Settings", "High Contrast, Large Text, Data Backup, Lock, Onboarding", 12),
        ToolCategory(12, "Bluetooth & Wi-Fi Comms", "Contactless", "BT/Wi-Fi Direct P2P Chat, Voice Walkie, Mesh Radar, Crypt", 30)
    )

    val tools = listOf(
        // CATEGORY 1: Light & Signaling (1-12)
        ToolItem(1, 1, "Flashlight Toggle", "Rear camera LED toggle with max brightness"),
        ToolItem(2, 1, "Strobe Mode", "Adjustable frequency strobe light slider (1-30Hz)"),
        ToolItem(3, 1, "SOS Morse Auto-Blink", "Automated ... --- ... emergency beacon pattern"),
        ToolItem(4, 1, "Custom Morse Blinker", "Convert custom text into flash/audio Morse signals"),
        ToolItem(5, 1, "Screen As Light", "Full screen white display at max brightness"),
        ToolItem(6, 1, "Colored Screen Light", "Red, Blue, Green, Amber screen light selector"),
        ToolItem(7, 1, "Red Night-Vision Mode", "Ultra-low intensity red light preserving night vision"),
        ToolItem(8, 1, "Screen Flash + Siren", "High visibility alternating flashing light + loud siren"),
        ToolItem(9, 1, "Emergency Beacon", "Periodic pulse flash every X seconds for long survival"),
        ToolItem(10, 1, "Wig-Wag Flash", "Alternating split-screen visual police pattern"),
        ToolItem(11, 1, "Brightness Emergency Lock", "Lock screen brightness to max for signal visibility"),
        ToolItem(12, 1, "Quick Tile & Widget Info", "Home screen widget and quick settings shortcut status"),

        // CATEGORY 2: Audio & Alerts (13-25)
        ToolItem(13, 2, "Loud Siren", "Air-raid, police, and emergency alarm sound generator"),
        ToolItem(14, 2, "Siren Pitch & Volume Control", "Adjustable frequency sweep and volume boost"),
        ToolItem(15, 2, "Whistle Sound Generator", "2800Hz high-decibel survival whistle tone"),
        ToolItem(16, 2, "Dog Deterrent Tone", "18.5kHz high frequency animal deterrent sound", true, "Hardware speaker limits may attenuate top frequencies"),
        ToolItem(17, 2, "Air Horn Sound", "Low harmonic dual-tone air horn burst"),
        ToolItem(18, 2, "Pre-recorded Voice Messages", "Quick audio clips: HELP, NEED WATER, CALL RESCUE"),
        ToolItem(19, 2, "Text-to-Speech Playback", "Convert written emergency text into spoken audio"),
        ToolItem(20, 2, "Loud Repeating Alarm Timer", "Interval timer triggering repeating emergency alert"),
        ToolItem(21, 2, "Morse Code Audio Beeper", "Synthesized audioMorse sound generator"),
        ToolItem(22, 2, "White Noise / Rain Generator", "Background audio for masking noise and sleeping"),
        ToolItem(23, 2, "Audio Memo Recorder", "Record voice notes using device microphone"),
        ToolItem(24, 2, "Offline Memo Playback", "Listen to saved voice recordings"),
        ToolItem(25, 2, "Ultrasonic Tone", "20kHz ultra-high frequency acoustic wave"),

        // CATEGORY 3: Radio & Broadcast (26-35)
        ToolItem(26, 3, "FM Radio Tuner", "System FM chip interface tuner", true, "FM Receiver chip unavailable. Fallback to system radio intents provided."),
        ToolItem(27, 3, "AM Radio Reference", "AM broadcast spectrum & emergency band guide", true, "AM reception not supported on smartphone hardware. Comprehensive reference provided."),
        ToolItem(28, 3, "Internet Radio Streams", "Fallback live online radio stream presets"),
        ToolItem(29, 3, "NOAA Weather Frequencies", "Complete offline list of NOAA WX1-WX7 channels"),
        ToolItem(30, 3, "Scanner Frequency Reference", "Police, Fire, EMS, Aviation, Marine emergency bands"),
        ToolItem(31, 3, "Bluetooth Mesh Link", "Quick link to Category 12 Bluetooth Mesh Messenger"),
        ToolItem(32, 3, "Wi-Fi Direct Link", "Quick link to Category 12 Wi-Fi Direct Peer Chat"),
        ToolItem(33, 3, "Offline Hotspot Walkie-Talkie", "Quick link to Category 12 Local Hotspot Walkie-Talkie"),
        ToolItem(34, 3, "Morse Flashlight Transmitter", "Visual signaling transmitter for line-of-sight comms"),
        ToolItem(35, 3, "Morse Audio Transmitter", "Acoustic Morse radio transmitter emulator"),

        // CATEGORY 4: Navigation & Sensors (36-48)
        ToolItem(36, 4, "Digital Compass", "Magnetometer compass with azimuth reading & calibration"),
        ToolItem(37, 4, "GPS Coordinates Display", "Real-time Latitude, Longitude, Altitude & Accuracy"),
        ToolItem(38, 4, "Save Waypoint", "Record current GPS position to Room DB"),
        ToolItem(39, 4, "Backtrack to Waypoint", "Directional arrow, bearing, and distance to saved point"),
        ToolItem(40, 4, "Breadcrumb Trail Recorder", "Track movement path on interactive canvas"),
        ToolItem(41, 4, "Altitude & Barometer", "Pressure elevation reading with GPS elevation fallback"),
        ToolItem(42, 4, "Pressure Trend Weather", "Short-term weather forecasting based on barometric pressure changes"),
        ToolItem(43, 4, "Temperature / Humidity Sensor", "Ambient environmental readings", true, "On-device ambient sensors unavailable. Shows battery temp & simulated estimate."),
        ToolItem(44, 4, "GPS Speedometer", "Real-time travel speed calculation in km/h or mph"),
        ToolItem(45, 4, "Step Counter", "Pedometer tracking step count and distance walked"),
        ToolItem(46, 4, "Sunrise / Sunset Calculator", "Offline astronomical solar calculations"),
        ToolItem(47, 4, "Moon Phase Calculator", "Offline lunar illumination and phase fraction"),
        ToolItem(48, 4, "Star Constellation Guide", "Offline celestial navigation map and guide"),

        // CATEGORY 5: Maps & Offline Data (49-56)
        ToolItem(49, 5, "Offline Map Viewer", "Interactive tactical canvas map viewer with grid overlay"),
        ToolItem(50, 5, "Cached Map Regions", "Save viewed offline regions to local storage"),
        ToolItem(51, 5, "Pin Drop / Custom Markers", "Drop markers with title, color, and description"),
        ToolItem(52, 5, "Distance & Area Measure", "Calculate straight-line distance and area between map pins"),
        ToolItem(53, 5, "Compass Overlay on Map", "Orient map relative to magnetic north"),
        ToolItem(54, 5, "Offline Region Search", "Locate bundled place-names and coordinates"),
        ToolItem(55, 5, "Organic Maps / OsmAnd Intent", "Link out to external offline map applications"),
        ToolItem(56, 5, "Coordinate Converter", "Convert between Degrees/Minutes/Seconds and Decimal Degrees"),

        // CATEGORY 6: Knowledge & Manuals (57-68)
        ToolItem(57, 6, "First-Aid Guide", "Offline field trauma, bleeding control, and burn manual"),
        ToolItem(58, 6, "CPR Step-by-Step", "Detailed resuscitation protocol with timing guide"),
        ToolItem(59, 6, "Water Purification Guide", "Boiling, SODIS, Chemical, and bio-filter instructions"),
        ToolItem(60, 6, "Fire-Starting Guide", "Ferro rod, tinder preparation, and improvised ignition"),
        ToolItem(61, 6, "Shelter-Building Guide", "Debris hut, A-frame, and insulation techniques"),
        ToolItem(62, 6, "Food Foraging Reference", "Edible wild plants, berries, and Universal Edibility Test"),
        ToolItem(63, 6, "Poisonous Plant Warnings", "Identification guide for poison ivy, hemlock, nightshade"),
        ToolItem(64, 6, "Knot-Tying Illustrated Guide", "Bowline, Square knot, Taut-line, Clove hitch instructions"),
        ToolItem(65, 6, "Navigation-by-Stars Guide", "Polaris, Southern Cross, and shadow-stick methods"),
        ToolItem(66, 6, "Emergency Signal Codes", "Ground-to-air visual signals and whistle codes"),
        ToolItem(67, 6, "Radio Frequency Bands", "NOAA, CB Channel 9, Marine VHF 16, FRS frequencies"),
        ToolItem(68, 6, "Offline Wikipedia Link-Out", "Kiwix reader integration link"),

        // CATEGORY 7: Health & Safety (69-78)
        ToolItem(69, 7, "First-Aid Checklist", "Checklist for emergency medical supplies"),
        ToolItem(70, 7, "CPR Metronome", "Visual and audible 100-120 bpm rhythm metronome"),
        ToolItem(71, 7, "Injury Log & Medical Notes", "Record injuries, treatments, and patient notes in DB"),
        ToolItem(72, 7, "Medication Reminder", "Local alarm reminders for vital meds"),
        ToolItem(73, 7, "Emergency Contacts", "Offline directory of emergency contacts"),
        ToolItem(74, 7, "Blood Type & Allergy Card", "Quick access medical emergency ID card"),
        ToolItem(75, 7, "Body Temperature Log", "Track patient fever and hypothermia trends"),
        ToolItem(76, 7, "Water Intake Tracker", "Log daily fluid consumption"),
        ToolItem(77, 7, "Wound Care Timer", "Bandage change and disinfection intervals"),
        ToolItem(78, 7, "Panic Button SMS", "Instant SMS alert sent to emergency contacts"),

        // CATEGORY 8: Power & Battery (79-86)
        ToolItem(79, 8, "Battery Percentage Monitor", "Real-time battery level, status, and health"),
        ToolItem(80, 8, "Battery Saver Mode Toggle", "Intent to system battery saver & app optimizations"),
        ToolItem(81, 8, "Power Consumption Estimator", "Estimate battery drain rate based on active sensors/screen"),
        ToolItem(82, 8, "Estimated Time Remaining", "Time remaining calculation from BatteryManager"),
        ToolItem(83, 8, "Low-Battery Alert", "Configurable threshold alert for critical power level"),
        ToolItem(84, 8, "Charge Reminder", "Alert reminder to recharge power banks"),
        ToolItem(85, 8, "Solar Charge Log", "Manual record of solar panel charging duration & Wh"),
        ToolItem(86, 8, "Battery Health Info", "Voltage, temperature, technology, and health stats"),

        // CATEGORY 9: Survival Utilities (87-98)
        ToolItem(87, 9, "On-Screen Ruler", "Calibratable millimeter & inch screen scale"),
        ToolItem(88, 9, "Protractor & Angle Measure", "Accelerometer-based slope and incline protractor"),
        ToolItem(89, 9, "Spirit Level", "2D bubble level using device accelerometer"),
        ToolItem(90, 9, "Magnifier", "Camera preview digital zoom glass"),
        ToolItem(91, 9, "Mirror", "Front camera view for signaling and grooming"),
        ToolItem(92, 9, "Stopwatch", "Precision millisecond timer with lap recording"),
        ToolItem(93, 9, "Countdown Timer", "Interval alarm countdown timer"),
        ToolItem(94, 9, "World Clock", "Offline multi-timezone clock and UTC display"),
        ToolItem(95, 9, "Calculator", "Full offline tactical scientific calculator"),
        ToolItem(96, 9, "Unit Converter", "Length, weight, temperature, pressure, and volume conversion"),
        ToolItem(97, 9, "Note-Taking", "Offline markdown-compatible notes stored in Room DB"),
        ToolItem(98, 9, "Checklist Maker", "Customizable Bug-Out Bag and supply checklists"),

        // CATEGORY 10: Emergency & SOS (99-108)
        ToolItem(99, 10, "One-Tap SOS", "Instant combination of strobe flash, loud siren, and contact SMS"),
        ToolItem(100, 10, "Emergency Speed-Dial", "One-touch intent to call emergency contacts"),
        ToolItem(101, 10, "Medical ID Lock Card", "Quick display card with blood type, allergies, and contacts"),
        ToolItem(102, 10, "Offline Emergency Numbers", "Global database of 911/112/999 regional emergency numbers"),
        ToolItem(103, 10, "I'm Safe SMS Broadcast", "Template SMS: 'I am safe at [Lat, Long]' to contacts"),
        ToolItem(104, 10, "Distress Signal Guide", "V, X, N, Y ground visual symbols and mirror codes"),
        ToolItem(105, 10, "Whistle Pattern Guide", "Standard 3-blast distress and response patterns"),
        ToolItem(106, 10, "Panic Wipe", "Wipe sensitive notes and chat logs instantly with confirmation"),
        ToolItem(107, 10, "Dead-Man Switch", "Check-in timer alerting contacts if inactive for X hours"),
        ToolItem(108, 10, "Emergency Battery Saver Mode", "Disable sensors, lower brightness, and dim UI to preserve battery"),

        // CATEGORY 11: Extras & QoL (109-120)
        ToolItem(109, 11, "Dark / High-Contrast Toggle", "Switch between Tactical Dark and Ultra High-Contrast OLED theme"),
        ToolItem(110, 11, "Large-Text Accessibility Mode", "Enlarge font scale for high readability"),
        ToolItem(111, 11, "Offline Onboarding Tutorial", "App walkthrough and feature usage guide"),
        ToolItem(112, 11, "Widget Info", "Guide and status for home screen survival widgets"),
        ToolItem(113, 11, "Quick Settings Tiles Info", "Android Quick Settings tile configuration guide"),
        ToolItem(114, 11, "App Shortcuts Info", "Home screen quick action launcher shortcuts"),
        ToolItem(115, 11, "App Lock (PIN/Biometric)", "Secure application with custom 4-digit PIN"),
        ToolItem(116, 11, "Data Backup & Local Export", "Export Room DB tables to local storage"),
        ToolItem(117, 11, "Import / Export JSON", "Import and export waypoints and notes as JSON files"),
        ToolItem(118, 11, "Multi-Language Support", "English, Spanish, and French language selector"),
        ToolItem(119, 11, "No-Internet-First Architecture", "System info verifying 100% offline self-containment"),
        ToolItem(120, 11, "Low-Storage Footprint Mode", "Memory and cache cleanup tool"),

        // CATEGORY 12: Bluetooth & Wi-Fi Comms (121-150)
        ToolItem(121, 12, "Bluetooth Text Chat", "Pair devices via Bluetooth RFCOMM socket to send messages offline"),
        ToolItem(122, 12, "Bluetooth Broadcast Message", "One-to-many discovery message broadcast"),
        ToolItem(123, 12, "BLE Proximity Beacon", "Broadcast BLE beacon alerting nearby paired phones"),
        ToolItem(124, 12, "Bluetooth File Transfer", "Send photos and manuals via Bluetooth socket"),
        ToolItem(125, 12, "Bluetooth Voice Memo Transfer", "Send recorded voice memos over Bluetooth"),
        ToolItem(126, 12, "Bluetooth Group Mesh", "Small mesh network spanning 3-8 devices"),
        ToolItem(127, 12, "Bluetooth SOS Broadcast", "Broadcast emergency SOS beacon to nearby devices"),
        ToolItem(128, 12, "Bluetooth Device Scanner", "Discover nearby phones running Apocalypse Toolkit"),
        ToolItem(129, 12, "Bluetooth Signal Strength Meter", "RSSI distance estimation gauge"),
        ToolItem(130, 12, "Bluetooth Dead-Drop", "Leave a location-bound digital message for nearby peers"),
        ToolItem(131, 12, "Wi-Fi Direct Peer Chat", "Direct phone-to-phone chat without router via WifiP2pManager"),
        ToolItem(132, 12, "Wi-Fi Direct Group Chat", "Multi-device local Wi-Fi peer network"),
        ToolItem(133, 12, "Host Local Hotspot Chat Server", "Host embedded chat server over mobile hotspot"),
        ToolItem(134, 12, "Wi-Fi Walkie-Talkie", "Push-to-talk live audio over local Wi-Fi socket"),
        ToolItem(135, 12, "Wi-Fi File Sharing", "AirDrop-style fast local network file transfer"),
        ToolItem(136, 12, "Wi-Fi Direct SOS Beacon", "Broadcast emergency signal over Wi-Fi Direct"),
        ToolItem(137, 12, "Local Mesh Chat", "Multi-hop best-effort Wi-Fi Direct mesh routing"),
        ToolItem(138, 12, "Shared Bulletin Board", "Local Wi-Fi notice board where connected peers post updates"),
        ToolItem(139, 12, "Local Map Sharing", "Send waypoints and pins to nearby connected devices"),
        ToolItem(140, 12, "Local Voice Broadcast", "One-to-many audio broadcast over Wi-Fi network"),
        ToolItem(141, 12, "Bluetooth + Wi-Fi Hybrid", "Auto-select best available wireless transport"),
        ToolItem(142, 12, "Store & Forward Messaging", "Delay-Tolerant Network (DTN) message hopping"),
        ToolItem(143, 12, "Offline Mesh Network", "Peer-to-peer BLE & Wi-Fi mesh networking"),
        ToolItem(144, 12, "Peer Discovery Radar", "Visual radar canvas showing nearby app users"),
        ToolItem(145, 12, "Encrypted Local Messaging", "End-to-end AES-256-GCM message encryption"),
        ToolItem(146, 12, "Group Channel System", "Join channels (e.g. Shelter-1, Water-Point, Medical)"),
        ToolItem(147, 12, "Emergency Broadcast Override", "High priority alert sent to all connected peers"),
        ToolItem(148, 12, "Local File Drop-Box", "Shared folder over local Wi-Fi network"),
        ToolItem(149, 12, "Voice Note Walkie-Talkie", "Push to record voice note -> auto-transmit to peers"),
        ToolItem(150, 12, "Offline Contact Exchange", "Share emergency contact card via Bluetooth/Wi-Fi")
    )
}
