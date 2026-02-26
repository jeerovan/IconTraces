
![IconTraces icon](app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp)

# IconTraces

An open-source Android icon pack built from clean, traced SVG icons, organized by app package ID and generated into an icon-pack-friendly Android resource structure.

## Overview

IconTraces ships icon sources as SVGs named using the target Android app package name (example: `com.spotify.music.svg`). The repository includes an automation script that converts these SVGs into WebP drawables used by the Android app, and a mapping file (`appfilter.xml`) used by supported launchers to apply icons.

## Repository layout

> Paths may vary slightly depending on your project structure, but the key files are:

- `packages.txt` — The master list of supported app package IDs (one per line).
- `*.svg` (source icons) — SVGs named as `<package_id>.svg`.
- `prepare-for-icon-pack.py` — Script that generates the required WebP resources.
- `res/drawable-nodpi/` — Generated foreground WebP icons: `fg_<sequence>.webp`.
- `res/xml/appfilter.xml` — Maps an app `ComponentInfo{...}` to a drawable name.

## Setup

### Requirements

- Android Studio (to build/install the app).
- Python 3.x (to run `prepare-for-icon-pack.py`).
- Script requires `ImageMagick` to convert svg to webp 

### Quick start

```bash
git clone https://github.com/jeerovan/IconTraces.git
cd IconTraces

# Open in Android Studio and build/install
```
## Adding new app icon

- Add package entry to `packages.txt` file and note the line number as sequence
  
  ``` com.example.app ```
  
- Add svg file in `svgs` folder

    ``` com.example.app.svg ```

- Run prepare-for-icon-pack.py
  
- update `appfilter.xml` as `app/src/main/res/xml/` with component info

  ```<item
    component="ComponentInfo{com.example.app/com.example.app.MainActivity}"
    drawable="fg_<sequence>" />```

## Modifying existing icon
  
- Modify the existing <package.svg> file in svgs folder
- Delete the old webp file from the `res/drawable-nodpi/` folder. (Check appfilter.xml if you are unsure which sequence number corresponds to the app)
- Run the python script and regenerate the webp drawable.
