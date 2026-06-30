from PIL import Image
import numpy as np
import os

# Load the sprite sheet
img = Image.open(r'res/objects/buildings.png').convert('RGBA')
arr = np.array(img)

# Background tan color (top-left pixel)
bg = arr[0, 0, :3]
r, g, b = int(bg[0]), int(bg[1]), int(bg[2])

# Make background transparent with a 20-unit tolerance
mask = (
    (np.abs(arr[:,:,0].astype(int) - r) < 20) &
    (np.abs(arr[:,:,1].astype(int) - g) < 20) &
    (np.abs(arr[:,:,2].astype(int) - b) < 20)
)
arr[mask, 3] = 0
transparent = Image.fromarray(arr)

# Find bounding boxes of each distinct building using connected components
alpha = arr[:,:,3]
from PIL import ImageFilter
import cv2

# Use OpenCV morphological operations to cluster nearby transparent blobs 
alpha_cv = (alpha == 0).astype(np.uint8) * 255  # 0 where sprites are

# Dilate slightly to merge nearby components of the same building
kernel = np.ones((15, 15), np.uint8)
dilated = cv2.dilate(cv2.bitwise_not(alpha_cv), kernel)

# Find contours on the merged mask
contours, _ = cv2.findContours(dilated, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

os.makedirs(r'res/objects/buildings_cropped', exist_ok=True)

i = 0
for c in contours:
    x, y, w, h = cv2.boundingRect(c)
    if w < 20 or h < 20:
        continue
    # Crop the *transparent* image
    roi = transparent.crop((x, y, x+w, y+h))
    roi.save(f'res/objects/buildings_cropped/sprite_{i}_{w}x{h}.png')
    i += 1

print(f"Extracted {i} sprites with clean transparent background")
