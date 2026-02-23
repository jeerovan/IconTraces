import subprocess
import os
import os

# --- CONFIGURATION ---
PROJECT_PATH = "/home/pi/AndroidStudioProjects/IconTraces"
DRAWABLES = os.path.join(PROJECT_PATH, "app/src/main/res/drawable-nodpi")
DRAWABLES_V26 = os.path.join(PROJECT_PATH, "app/src/main/res/drawable-anydpi-v26")
SVGS = os.path.join(PROJECT_PATH, "svgs")
APPFILTER = os.path.join(PROJECT_PATH, "app/src/main/res/xml/appfilter.xml")


def prepare_graphic(package, sequence):
    source = f"{SVGS}/{package}.svg"
    destination = f"{DRAWABLES}/fg_{sequence}.webp"
    if os.path.exists(destination):
        return
    cmd = [
        "convert",
        "-background",
        "none",
        "-density",
        "1200",
        "-resize",
        "192x192",
        str(source),
        str(destination),
    ]
    subprocess.run(
        cmd, check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL
    )


def generate_adaptive(sequence):
    header = '<?xml version="1.0" encoding="UTF-8"?>\n'
    output_file = f"{DRAWABLES_V26}/_{sequence}.xml"
    if os.path.exists(output_file):
        return
    item_str = (
        f'<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android"> \n'
        f'    <background android:drawable="@drawable/bg_adaptive" /> \n'
        f"    <foreground> \n"
        f"      <inset \n"
        f'         android:drawable="@drawable/fg_{sequence}" \n'
        f'         android:inset="20%" /> \n'
        f"    </foreground> \n"
        f"</adaptive-icon>"
    )
    try:
        with open(output_file, "w", encoding="utf-8") as f:
            f.write(header)
            f.write(item_str)

        print(f"✅ Successfully generated '{output_file}'")
    except IOError as e:
        print(f"❌ Error writing file: {e}")


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument(
        "-f", "--file", help="file containing package names", required=True
    )
    args = parser.parse_args()
    FILE = args.file
    try:
        sequence = 1
        with open(FILE, "r") as file:
            for line in file:
                package = line.strip()
                generate_adaptive(sequence)
                prepare_graphic(package, sequence)
                sequence += 1
    except Exception as e:
        print(f"❌ Error during processing: {e}")
