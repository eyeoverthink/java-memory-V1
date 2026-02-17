"""
Simple test to verify OpenCV window can display
"""
import cv2
import numpy as np
import time

print("Testing OpenCV window display...")

# Create a simple test window
window_name = "Max Headroom Test"
cv2.namedWindow(window_name, cv2.WINDOW_NORMAL)
cv2.resizeWindow(window_name, 800, 600)

# Create test image with text
img = np.zeros((600, 800, 3), dtype=np.uint8)
img[:] = (0, 20, 0)  # Dark green background

# Add text
cv2.putText(img, "MAX HEADROOM BROADCAST TEST", (150, 250), 
            cv2.FONT_HERSHEY_SIMPLEX, 1.2, (0, 255, 0), 2)
cv2.putText(img, "Window is working!", (250, 350), 
            cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 200), 2)
cv2.putText(img, "Press ESC to close", (250, 450), 
            cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 180, 0), 1)

print("Window should be displayed now.")
print("Press ESC in the window to close it.")

# Display and wait for ESC
while True:
    cv2.imshow(window_name, img)
    key = cv2.waitKey(30) & 0xFF
    
    if key == 27:  # ESC
        print("ESC pressed - closing window")
        break

cv2.destroyAllWindows()
print("Test complete!")
