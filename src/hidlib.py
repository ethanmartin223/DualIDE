# ------------------ # Imports # ------------------ #
import winrawin
import tkinter as tk

from winrawin import Keyboard

# ------------------ # Find HID Devices # ------------------ #
for iteration, device in enumerate(winrawin.list_devices()):
    if isinstance(device, winrawin.Keyboard):
        print(f"[{iteration}] {device.keyboard_type} with {device.num_keys} keys name='{device.path}'")
print()

KEYBOARD_1 = winrawin.list_devices()[6]
KEYBOARD_2 = winrawin.list_devices()[0]
keyboards = []


# ------------------ # Functions # ------------------ #
def handle_event(e: winrawin.RawInputEvent):
    if e.device not in keyboards and isinstance(e.device, Keyboard):
        keyboards.append(e.device)
        print(f"\nNew Device detected: {e.device}\n")
    if not (e.event_type == 'move' or e.event_type == 'down'):
        print(e.code, end=',')


# ------------------ # Main Window # ------------------ #
window = tk.Tk()
window.geometry("50x50")

# ------------------ # Main Loop and Routines # ------------------ #
print(window.winfo_id())
winrawin.hook_raw_input_for_window(window.winfo_id(), handle_event)
window.mainloop()
