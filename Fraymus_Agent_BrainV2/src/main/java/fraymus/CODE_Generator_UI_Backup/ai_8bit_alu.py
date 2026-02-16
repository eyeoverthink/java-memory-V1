import time

# === 8-Bit ALU (Arithmetic Logic Unit) ===
class ALU:
    def __init__(self):
        self.result = 0
        self.flags = {"zero": 0, "carry": 0}

    def add(self, a, b):
        result = a + b
        self.flags["carry"] = 1 if result > 255 else 0
        self.result = result & 0xFF
        return self.result

    def sub(self, a, b):
        result = a - b
        self.flags["zero"] = 1 if result == 0 else 0
        self.result = result & 0xFF
        return self.result

    def multiply(self, a, b):
        result = a * b
        self.flags["carry"] = 1 if result > 255 else 0
        self.result = result & 0xFF
        return self.result

    def divide(self, a, b):
        if b == 0:
            return 0  # Avoid division by zero
        self.result = a // b
        return self.result

# === 8-Bit Program Counter ===
class ProgramCounter:
    def __init__(self):
        self.pc = 0  

    def increment(self):
        self.pc = (self.pc + 1) & 0xFF  # Expanded to 256 bytes

    def load(self, value):
        self.pc = value & 0xFF  # Ensures valid jump target

# === 8-Bit Registers ===
class Register:
    def __init__(self):
        self.value = 0  

    def load(self, data):
        self.value = data & 0xFF  

    def read(self):
        return self.value

# === 8-Bit RAM (Expanded to 256 bytes) ===
class RAM:
    def __init__(self):
        self.memory = [0] * 256  

    def load(self, address, value):
        if 0 <= address < 256:
            self.memory[address] = value & 0xFF
        else:
            print(f"ERROR: Memory write out of bounds at address {address}")

    def read(self, address):
        if 0 <= address < 256:
            return self.memory[address]
        else:
            print(f"ERROR: Memory read out of bounds at address {address}")
            return 0

# === Stack for Function Calls ===
class Stack:
    def __init__(self):
        self.stack = []

    def push(self, value):
        self.stack.append(value)

    def pop(self):
        return self.stack.pop() if self.stack else 0

# === Instruction Decoder & Execution ===
class InstructionDecoder:
    def __init__(self, alu, registers, pc, ram, stack):
        self.alu = alu
        self.registers = registers
        self.pc = pc
        self.ram = ram
        self.stack = stack

        self.instructions = {
            0x01: self.LOAD_A,
            0x02: self.LOAD_B,
            0x03: self.ADD,
            0x04: self.STORE,
            0x05: self.OUT,
            0x06: self.HALT,
            0x07: self.JUMP,
            0x08: self.MULTIPLY,
            0x09: self.DIVIDE,
            0x0A: self.CALL,
            0x0B: self.RETURN
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

    def STORE(self):
        address = self.ram.read(self.pc.pc + 1)
        self.ram.load(address, self.registers[0].read())
        self.pc.increment()
        self.pc.increment()

    def OUT(self):
        print(f"OUTPUT: {self.registers[0].read()}")
        self.pc.increment()

    def HALT(self):
        print("CPU HALTED")
        return False  

    def JUMP(self):
        jump_address = self.ram.read(self.pc.pc + 1) & 0xFF
        self.pc.load(jump_address)

    def MULTIPLY(self):
        result = self.alu.multiply(self.registers[0].read(), self.registers[1].read())
        self.registers[0].load(result)
        self.pc.increment()

    def DIVIDE(self):
        result = self.alu.divide(self.registers[0].read(), self.registers[1].read())
        self.registers[0].load(result)
        self.pc.increment()

    def CALL(self):
        return_address = self.pc.pc + 2
        self.stack.push(return_address)
        jump_address = self.ram.read(self.pc.pc + 1) & 0xFF
        self.pc.load(jump_address)

    def RETURN(self):
        self.pc.load(self.stack.pop())

    def decode_execute(self):
        while True:
            instruction = self.ram.read(self.pc.pc)
            if instruction in self.instructions:
                if self.instructions[instruction]() == False:
                    break
            else:
                print(f"ERROR: Unknown instruction {instruction} at address {self.pc.pc}")
                break

# === AI-Powered Assembler ===
def ai_assembler(source_code):
    opcode_map = {
        "LOAD_A": 0x01, "LOAD_B": 0x02, "ADD": 0x03, "STORE": 0x04,
        "OUT": 0x05, "HALT": 0x06, "JUMP": 0x07, "MULTIPLY": 0x08,
        "DIVIDE": 0x09, "CALL": 0x0A, "RETURN": 0x0B
    }
    
    machine_code = []
    for line in source_code:
        parts = line.split()
        if parts[0] in opcode_map:
            machine_code.append(opcode_map[parts[0]])
            if len(parts) > 1:
                machine_code.append(int(parts[1]))
    
    return machine_code

# === Running AI 8-Bit Computer ===
class AIBreadboardComputer:
    def __init__(self):
        self.alu = ALU()
        self.pc = ProgramCounter()
        self.registers = [Register(), Register()]
        self.ram = RAM()
        self.stack = Stack()
        self.decoder = InstructionDecoder(self.alu, self.registers, self.pc, self.ram, self.stack)

    def load_program(self, program):
        for i in range(len(program)):
            self.ram.load(i, program[i])

    def run(self):
        self.decoder.decode_execute()

# === Example Program (Multiplication & Function Call) ===
assembly_code = [
    "LOAD_A 100",
    "LOAD_B 101",
    "MULTIPLY",
    "OUT",
    "HALT"
]

program = ai_assembler(assembly_code)

ai_computer = AIBreadboardComputer()
ai_computer.load_program(program)
ai_computer.run()
