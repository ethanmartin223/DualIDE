# ------------------ # Imports # ------------------ #
import winrawin
import tkinter as tk
from winrawin import Keyboard

# ------------------ # Vars # ------------------ #
keyboards = []


# ------------------ # Functions # ------------------ #
# translate https://www.win.tue.nl/~aeb/linux/kbd/scancodes-14.html

def handle_event(e: winrawin.RawInputEvent):
    if e.device not in keyboards and isinstance(e.device, Keyboard):
        keyboards.append(e.device)
        print(f"\nNew Device detected: {e.device}\n")
    if e.device in keyboards and e.event_type == 'down':
        print(e.code, end=', ')


# ------------------ # Main Window # ------------------ #
window = tk.Tk()
window.geometry("0x0")

# ------------------ # Main Loop and Routines # ------------------ #
winrawin.hook_raw_input_for_window(window.winfo_id(), handle_event)
window.mainloop()
