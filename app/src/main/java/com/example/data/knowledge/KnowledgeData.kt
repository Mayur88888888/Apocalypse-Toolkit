package com.example.data.knowledge

data class ManualSection(
    val title: String,
    val category: String,
    val iconName: String,
    val summary: String,
    val steps: List<String>,
    val warnings: String = ""
)

object KnowledgeData {
    val manuals = listOf(
        ManualSection(
            title = "First Aid & Triage Guide",
            category = "FIRST_AID",
            iconName = "LocalHospital",
            summary = "Essential field first aid for trauma, bleeding, fracture, and shock in grid-down scenarios.",
            steps = listOf(
                "1. Direct Pressure: Apply firm, continuous pressure to bleeding wounds using clean cloth/gauze for at least 10 minutes.",
                "2. Tourniquet Application: For arterial limb bleeding, place tourniquet 2-3 inches above injury (never over joints). Tighten until bleeding stops and record time.",
                "3. Airway & Recovery Position: Clear airway. If unconscious but breathing, place victim on their side with head tilted back to prevent aspiration.",
                "4. Burn Treatment: Cool burns with clean water for 10-20 mins. Cover loosely with sterile dressing. Do NOT pop blisters or apply butter.",
                "5. Shock Management: Keep patient warm with blankets, elevate legs 12 inches if no spinal injury suspected, reassure and monitor pulse."
            ),
            warnings = "Always check scene safety before treating. Assume spinal injury in major trauma."
        ),
        ManualSection(
            title = "CPR & Resuscitation Protocol",
            category = "FIRST_AID",
            iconName = "Favorite",
            summary = "Standard hands-only & full CPR procedure at 100-120 compressions per minute.",
            steps = listOf(
                "1. Check responsiveness & breathing (5-10 seconds).",
                "2. Place heel of hand on center of chest (lower half of sternum), interlock fingers of second hand.",
                "3. Compress hard and fast: at least 2 inches deep, 100-120 beats per minute (use app CPR metronome).",
                "4. Allow complete chest recoil between compressions.",
                "5. Continue 30 compressions : 2 rescue breaths if trained, or continuous chest compressions until pulse returns."
            ),
            warnings = "Perform compressions on a firm, flat surface."
        ),
        ManualSection(
            title = "Water Purification Methods",
            category = "SURVIVAL_MANUAL",
            iconName = "WaterDrop",
            summary = "Four reliable methods to produce potable water from natural sources.",
            steps = listOf(
                "1. Boiling (Gold Standard): Bring water to a rolling boil for 1 full minute (3 mins above 6,500ft altitude). Kills bacteria, viruses, parasites.",
                "2. SODIS Solar Disinfection: Fill clear PET plastic bottles. Expose to direct sunlight for 6 hours (or 2 days if cloudy). UV-A kills pathogens.",
                "3. Chemical Treatment: Add 2 drops of unscented household bleach (5-6% sodium hypochlorite) per 1 liter of clear water. Let stand for 30 minutes.",
                "4. DIY Bio-Sand Filter: Layer coarse gravel, charcoal, and fine sand in a container with bottom drain to filter sediment before boiling."
            ),
            warnings = "Filtering removes sediment, but boiling/chemical treatment is needed to destroy pathogens."
        ),
        ManualSection(
            title = "Fire-Starting Techniques",
            category = "SURVIVAL_MANUAL",
            iconName = "LocalFireDepartment",
            summary = "Methods to ignite fire without matches using primitive and improvised tools.",
            steps = listOf(
                "1. Tinder Preparation: Gather dry tinder (birch bark, char cloth, cotton balls with petroleum jelly, dried grass).",
                "2. Ferro Rod Strike: Hold striker at 45-degree angle. Scrape firmly down rod into tinder bundle.",
                "3. Battery & Foil / Steel Wool: Touch 9V or AA battery contacts to fine steel wool or gum wrapper foil bridge to create glowing ember.",
                "4. Magnifying Lens: Focus sunlight into concentrated point on dark tinder bundle until smoke appears; blow gently to ignite."
            ),
            warnings = "Prepare tinder, kindling, and fuelwood before striking fire."
        ),
        ManualSection(
            title = "Shelter-Building Field Guide",
            category = "SURVIVAL_MANUAL",
            iconName = "Cabin",
            summary = "Emergency shelter designs for heat preservation and storm protection.",
            steps = listOf(
                "1. Debris Hut: Lean 9-10ft ridge pole against tree crotch. Rest ribs along sides. Cover with 2-3 feet of dry leaves and pine needles for insulation.",
                "2. Tarp A-Frame: String ridgeline rope between two trees at waist height. Stake sides to ground. Elevate floor with boughs.",
                "3. Ground Insulation: NEVER sleep directly on bare ground. Build a bed layer of dry boughs/leaves at least 6 inches thick."
            ),
            warnings = "Avoid low hollows (cold air/flooding) and widowmaker trees with dead overhead branches."
        ),
        ManualSection(
            title = "Food Foraging Reference",
            category = "FORAGING",
            iconName = "Restaurant",
            summary = "Wild edible plants, berries, and Universal Edibility Test safety rules.",
            steps = listOf(
                "1. Dandelion: Entire plant edible (leaves, yellow flowers, boiled roots).",
                "2. Cattail ('Supermarket of the Swamp'): Starch-rich rootstocks, tender shoots, and young pollen heads edible.",
                "3. Plantain (Plantago): Ribbed leaves edible raw or boiled; crushed leaves soothe sting/burns.",
                "4. Universal Edibility Test: Test unknown plant in steps: Smell -> Skin rub -> Lip touch -> Tongue test -> Chew 15m -> Swallow small bite -> Wait 8 hours."
            ),
            warnings = "NEVER eat wild mushrooms unless 100% verified. Avoid plants with milky sap, umbrella flowers, or bitter almond scent."
        ),
        ManualSection(
            title = "Poisonous Plant Warnings",
            category = "FORAGING",
            iconName = "Warning",
            summary = "Identification and treatment for hazardous vegetation.",
            steps = listOf(
                "1. Poison Ivy / Oak / Sumac: 'Leaves of three, let it be.' Urushiol oil causes severe blistering rash. Wash skin with soap & cool water immediately.",
                "2. Poison Hemlock: Fern-like leaves, purple spots on stems, white umbrella flowers. Fatal if ingested.",
                "3. Deadly Nightshade (Belladonna): Shiny black berries, purple bell flowers. Causes delirium and respiratory failure."
            ),
            warnings = "If exposed to urushiol oil, do NOT use hot water (opens pores)."
        ),
        ManualSection(
            title = "Knot-Tying Illustrated Guide",
            category = "UTILITY",
            iconName = "Timeline",
            summary = "Core survival knots for rigging, climbing, and securing gear.",
            steps = listOf(
                "1. Bowline (King of Knots): Forms fixed loop at rope end. Does not slip under tension.",
                "2. Square Knot (Reef Knot): Connects two lines of equal diameter ('Right over left, left over right').",
                "3. Taut-Line Hitch: Adjustable friction hitch for tent guy lines and load tie-downs.",
                "4. Clove Hitch: Secures rope around post or timber."
            ),
            warnings = "Inspect ropes for fraying before bearing load."
        ),
        ManualSection(
            title = "Navigation-by-Stars Guide",
            category = "NAVIGATION",
            iconName = "Bedtime",
            summary = "Locating True North and celestial navigation without a compass.",
            steps = listOf(
                "1. Northern Hemisphere (Polaris): Locate Big Dipper. Draw line through outer two stars of cup (Merak & Dubhe) extending 5x distance straight to Polaris (North Star).",
                "2. Southern Hemisphere (Southern Cross): Draw line along long axis of Southern Cross, extend 4.5x. Point directly below in horizon is True South.",
                "3. Shadow-Stick Method: Plant stick in flat ground. Mark shadow tip (Point A). Wait 15 mins, mark new tip (Point B). Line A to B runs West to East."
            )
        ),
        ManualSection(
            title = "Emergency Signal Codes",
            category = "SIGNALING",
            iconName = "Shield",
            summary = "Ground-to-Air visual symbols and whistle signals.",
            steps = listOf(
                "1. Ground V: 'Require Assistance'.",
                "2. Ground X: 'Require Medical Aid'.",
                "3. Ground N: 'No / Negative'. Ground Y: 'Yes / Affirmative'.",
                "4. Whistle SOS: 3 short blasts, 3 long blasts, 3 short blasts.",
                "5. Whistle 3 Blasts: International emergency distress signal."
            )
        ),
        ManualSection(
            title = "Radio Frequency Reference",
            category = "RADIO",
            iconName = "Radio",
            summary = "Emergency radio bands, NOAA frequencies, and marine channels.",
            steps = listOf(
                "1. NOAA Weather Radio Frequencies: 162.400 MHz (WX1), 162.425 MHz (WX2), 162.450 MHz (WX3), 162.475 MHz (WX4), 162.500 MHz (WX5), 162.525 MHz (WX6), 162.550 MHz (WX7).",
                "2. CB Emergency Channel: Channel 9 (27.065 MHz AM).",
                "3. Marine VHF Emergency: Channel 16 (156.800 MHz).",
                "4. FRS/GMRS Channel 1: 462.5625 MHz (Common Calling)."
            )
        )
    )

    val emergencyNumbers = listOf(
        "North America (US/CA)" to "911",
        "European Union & UK" to "112",
        "United Kingdom" to "999",
        "Australia" to "000",
        "New Zealand" to "111",
        "Japan" to "119 / 110",
        "India" to "112",
        "International Satellite Emergency" to "112 / 911"
    )
}
