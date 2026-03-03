import shutil
from pathlib import Path
import subprocess
import os

# --- Configuration ---
PROJECT_PATH = Path("/home/pi/AndroidStudioProjects/IconTraces")
SVGS = os.path.join(PROJECT_PATH, "svgs")
SOURCE_PART = "app"
RESPATH = "src/main/res"

DRAWABLE = "drawable-anydpi-v26"
WEBP = "drawable-nodpi"


def prepare_graphic(package, destination):
    source = f"{SVGS}/{package}.svg"
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


def generate_adaptive(sequence, destination):
    header = '<?xml version="1.0" encoding="UTF-8"?>\n'
    if os.path.exists(destination):
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
        with open(destination, "w", encoding="utf-8") as f:
            f.write(header)
            f.write(item_str)

        print(f"✅ Successfully generated '{destination}'")
    except IOError as e:
        print(f"❌ Error writing file: {e}")


def get_target_module(number: int) -> str:
    """Determine the destination module based on the file number."""
    if number <= 9000:
        return "traces_1_9k"
    elif number <= 18000:
        return "traces_9_18k"
    elif number <= 27000:
        return "traces_18_27k"
    elif number <= 36000:
        return "traces_27_36k"


def generate_drawables(package, number: int):
    # Determine which module this number belongs to
    target_part = get_target_module(number)

    # Construct filenames using f-strings
    xml_filename = f"_{number}.xml"
    webp_filename = f"fg_{number}.webp"

    # Define source paths
    # Result: app/src/main/res/drawable-anydpi-v26/_1.xml
    src_xml = PROJECT_PATH / SOURCE_PART / RESPATH / DRAWABLE / xml_filename
    src_webp = PROJECT_PATH / SOURCE_PART / RESPATH / WEBP / webp_filename

    # Define destination directories
    dest_xml_dir = PROJECT_PATH / target_part / RESPATH / DRAWABLE
    dest_webp_dir = PROJECT_PATH / target_part / RESPATH / WEBP

    # Ensure destination directories exist before attempting to move
    dest_xml_dir.mkdir(parents=True, exist_ok=True)
    dest_webp_dir.mkdir(parents=True, exist_ok=True)

    # Define final destination file paths
    dest_xml = dest_xml_dir / xml_filename
    dest_webp = dest_webp_dir / webp_filename

    # Generate XML file if it does not exist
    if not dest_xml.exists():
        generate_adaptive(number, str(dest_xml))
    # Generate WebP file if it does not exist
    if not dest_webp.exists():
        prepare_graphic(package, str(dest_webp))


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
                generate_drawables(package, sequence)
                sequence += 1
    except Exception as e:
        print(f"❌ Error during processing: {e}")
