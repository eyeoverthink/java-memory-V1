import tkinter as tk
from tkinter import scrolledtext
import time

# === 8-Bit CPU Components ===
class ALU:
    def __init__(self):
        self.result = 0

    def add(self, a, b):
        return (a + b) & 0xFF

    def multiply(self, a, b):
        return (a * b) & 0xFF

class ProgramCounter:
    def __init__(self):
        self.pc = 0  

    def increment(self):
        self.pc = (self.pc + 1) & 0xFF

    def load(self, value):
        self.pc = value & 0xFF

class Register:
    def __init__(self):
        self.value = 0  

    def load(self, data):
        self.value = data & 0xFF  

    def read(self):
        return self.value

class RAM:
    def __init__(self):
        self.memory = [0] * 256  

    def load(self, address, value):
        self.memory[address] = value & 0xFF

    def read(self, address):
        return self.memory[address]

# === AI Assembler ===
def ai_assembler(source_code):
    opcode_map = {
        "LOAD_A": 0x01, "LOAD_B": 0x02, "ADD": 0x03, "MULTIPLY": 0x04, 
        "STORE": 0x05, "OUT": 0x06, "HALT": 0x07
    }
    
    machine_code = []
    for line in source_code:
        parts = line.split()
        if parts[0] in opcode_map:
            machine_code.append(opcode_map[parts[0]])
            if len(parts) > 1:
                machine_code.append(int(parts[1]))
    
    return machine_code

# === CPU Instruction Set ===
class InstructionDecoder:
    def __init__(self, alu, registers, pc, ram, output_callback):
        self.alu = alu
        self.registers = registers
        self.pc = pc
        self.ram = ram
        self.output_callback = output_callback

        self.instructions = {
            0x01: self.LOAD_A, 0x02: self.LOAD_B, 0x03: self.ADD, 
            0x04: self.MULTIPLY, 0x05: self.STORE, 0x06: self.OUT, 0x07: self.HALT
        }

    def LOAD_A(self):
        address = self.ram.read(self.pc.pc + 1)
        self.registers[0].load(self.ram.read(address))
        self.pc.increment()
        self.pc.increment()

    def LOAD_B(self):
        address = self.ram.read(self.pc.pc + 1)
        self.registers[1].load(self.ram.read(address))
        self.pc.increment()
        self.pc.increment()

    def ADD(self):
        result = self.alu.add(self.registers[0].read(), self.registers[1].read())
        self.registers[0].load(result)
        self.pc.increment()

    def MULTIPLY(self):
        result = self.alu.multiply(self.registers[0].read(), self.registers[1].read())
        self.registers[0].load(result)
        self.pc.increment()

    def STORE(self):
        address = self.ram.read(self.pc.pc + 1)
        self.ram.load(address, self.registers[0].read())
        self.pc.increment()
        self.pc.increment()

    def OUT(self):
        output_value = self.registers[0].read()
        self.output_callback(f"OUTPUT: {output_value}")
        self.pc.increment()

    def HALT(self):
        self.output_callback("CPU HALTED")
        return False  

    def execute_step(self):
        instruction = self.ram.read(self.pc.pc)
        if instruction in self.instructions:
            if self.instructions[instruction]() == False:
                return False
        return True

# === GUI-Based AI 8-Bit Computer ===
class AIBreadboardComputer:
    def __init__(self, gui_output_callback):
        self.alu = ALU()
        self.pc = ProgramCounter()
        self.registers = [Register(), Register()]
        self.ram = RAM()
        self.decoder = InstructionDecoder(self.alu, self.registers, self.pc, self.ram, gui_output_callback)

    def load_program(self, program):
        for i in range(len(program)):
            self.ram.load(i, program[i])

    def step(self):
        return self.decoder.execute_step()

# === GUI Class with RAM Editing & Live Debugging ===
class AIComputerGUI:
    def __init__(self, root):
        self.computer = AIBreadboardComputer(self.append_output)
        self.running = False

        root.title("AI 8-Bit Breadboard Computer")

        self.code_entry = scrolledtext.ScrolledText(root, height=10, width=50)
        self.code_entry.pack()

        self.load_button = tk.Button(root, text="Assemble & Load", command=self.load_code)
        self.load_button.pack()

        self.run_button = tk.Button(root, text="Run Program", command=self.run_program)
        self.run_button.pack()

        self.step_button = tk.Button(root, text="Step", command=self.step_program)
        self.step_button.pack()

        # === Manual RAM Editor ===
        self.ram_editor_label = tk.Label(root, text="Edit RAM: Address & Value")
        self.ram_editor_label.pack()

        self.ram_address_entry = tk.Entry(root, width=5)
        self.ram_address_entry.pack(side=tk.LEFT)

        self.ram_value_entry = tk.Entry(root, width=5)
        self.ram_value_entry.pack(side=tk.LEFT)

        self.set_ram_button = tk.Button(root, text="Set RAM", command=self.set_ram_value)
        self.set_ram_button.pack(side=tk.LEFT)

        # === Output Console ===
        self.output_text = scrolledtext.ScrolledText(root, height=10, width=50)
        self.output_text.pack()

        # === Register & RAM Display ===
        self.register_display = tk.Label(root, text="Registers: A=0, B=0", font=("Arial", 12))
        self.register_display.pack()

        self.ram_display = scrolledtext.ScrolledText(root, height=10, width=50)
        self.ram_display.pack()

    def load_code(self):
        assembly_code = self.code_entry.get("1.0", tk.END).strip().split("\n")
        machine_code = ai_assembler(assembly_code)
        self.computer.load_program(machine_code)
        self.append_output("Program Loaded.")
        self.update_registers()
        self.update_ram()

    def run_program(self):
        self.running = True
        while self.running:
            if not self.computer.step():
                self.running = False
            self.update_registers()
            self.update_ram()
            root.update()
            time.sleep(0.5)

    def step_program(self):
        if not self.computer.step():
            self.append_output("Program Completed.")
        self.update_registers()
        self.update_ram()

    def set_ram_value(self):
        address = int(self.ram_address_entry.get())
        value = int(self.ram_value_entry.get())
        self.computer.ram.load(address, value)
        self.append_output(f"RAM[{address}] set to {value}")
        self.update_ram()

    def append_output(self, text):
        self.output_text.insert(tk.END, text + "\n")
        self.output_text.see(tk.END)

    def update_registers(self):
        a_value = self.computer.registers[0].read()
        b_value = self.computer.registers[1].read()
        self.register_display.config(text=f"Registers: A={a_value}, B={b_value}")

    def update_ram(self):
        self.ram_display.delete("1.0", tk.END)
        ram_data = "\n".join([f"{i:03}: {self.computer.ram.read(i)}" for i in range(256)])
        self.ram_display.insert(tk.END, ram_data)

# === Start the GUI ===
root = tk.Tk()
gui = AIComputerGUI(root)
root.mainloop()
