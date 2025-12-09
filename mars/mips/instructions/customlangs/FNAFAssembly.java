package mars.mips.instructions.customlangs;
import mars.simulator.*;
import mars.mips.hardware.*;
import mars.mips.instructions.syscalls.*;
import mars.*;
import mars.util.*;
import java.util.*;
import java.io.*;
import mars.mips.instructions.*;
import java.util.Random;

public class FNAFAssembly extends CustomAssembly {
    @Override
    public String getName(){
        return "Five Nights at Freddy's Assembly";
    }

    @Override
    public String getDescription(){
        return "Simulate a typical night shift as a security guard at Freddy Fazbear's Pizzeria!";
    }

    @Override
    protected void populate() {

        // phone guy dialog cut down
        SystemIO.printString("Hello? Hello hello?" + "\n" +
                "\nUh, I wanted to record a message for you, to help you get settled in on your first night."
                + "\n" + "Uh, let’s see, first there’s an introductory greeting from the company, that I’m supposed to read."
        + "\n" + "Um, ''Welcome to Freddy Fazbear’s Pizza, a magical place for kids and grown-ups alike, where fantasy and fun come to life.''" +
                "\n" + "Uh, the animatronic characters here, do get a bit quirky at night. " +
                "So, just be aware, the characters do tend to wander a bit." + "\n" +
                "\nUh, now concerning your safety.\nIf they happen to see you after hours probably won’t recognize you as a person. They’ll pr-They’ll most likely see you as a metal endoskeleton without its costume on." +
                "\nNow, since that’s against the rules here at Freddy Fazbear’s Pizza, " +
                "they’ll probably try to…forcefully stuff you inside a Freddy Fazbear suit.\n\n" +
                "But hey, first day should be a breeze. I’ll chat with you tomorrow." +
                "\nUh, check those cameras, and remember to close the doors only if absolutely necessary. Gotta conserve power.\n");

        instructionList.add(
                new BasicInstruction("ring label",
                        "Ring : Call phone guy and transmit a string message stored at the label",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "001101 00000 00000 ffffffffffffffff",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                char ch = 0;
                                // Get the name of the label from the token list
                                String label = statement.getOriginalTokenList().get(1).getValue();
                                // Look up the label in the program symbol table to get its address
                                int byteAddress = Globals.program.getLocalSymbolTable().getAddressLocalOrGlobal(label);

                                try
                                {
                                    ch = (char) Globals.memory.getByte(byteAddress);
                                    // won't stop until NULL byte reached!
                                    while (ch != 0)
                                    {
                                        SystemIO.printString(new Character(ch).toString());
                                        byteAddress++;
                                        ch = (char) Globals.memory.getByte(byteAddress);
                                    }
                                }
                                catch (AddressErrorException e)
                                {
                                    throw new ProcessingException(statement, e);
                                }

                            }

                        }));

        instructionList.add(
                new BasicInstruction("ans $t0",
                        "Answer Phone Guy : Read an integer and store the value in $t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 fffff 00000 001110",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // put value 5 in $v0, execute syscall at $v0
                                RegisterFile.updateRegister(2, 5);
                                Globals.instructionSet.findAndSimulateSyscall(RegisterFile.getValue(2),statement);
                                int value = RegisterFile.getValue(2);
                                // reset
                                RegisterFile.updateRegister(2, 0);
                                int[] operands = statement.getOperands();

                                RegisterFile.updateRegister(operands[0], value);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("printp $t0",
                        "Print Power : Print the integer value in $t0",
                        BasicInstructionFormat.I_FORMAT,
                        "010110 fffff 00000 00000 00000 000000",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // put value 1 in $v0, execute syscall at $v0, put value in $t0 in $a0
                                int[] operands = statement.getOperands();
                                RegisterFile.updateRegister(2, 1);
                                RegisterFile.updateRegister(4, RegisterFile.getValue(operands[0]));

                                // perform syscall
                                Globals.instructionSet.findAndSimulateSyscall(RegisterFile.getValue(2),statement);

                                // reset $a0 and $v0
                                RegisterFile.updateRegister(4, 0);
                                RegisterFile.updateRegister(2, 0);

                            }
                        }));

        instructionList.add(
                new BasicInstruction("gen $t0,-100",
                        "Generate power : Generate power in the form of an immediate value into $t0",
                        BasicInstructionFormat.I_FORMAT,
                        "010001 fffff 00000 ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int value = operands[1] << 16 >> 16;
                                RegisterFile.updateRegister(operands[0], value);

                            }
                        }));

        instructionList.add(
                new BasicInstruction("roll $s0",
                        "Roll Animatronic : Randomize the animatronic that is active in $s0",
                        BasicInstructionFormat.I_FORMAT,
                        "000001 fffff 00000 00000 00000 000000",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                Random random = new Random();
                                int roll = 0;
                                while (roll == 0) {
                                    roll = random.nextInt(5); // want values 1-4
                                }

                                // put random value into $anim
                                int[] operands = statement.getOperands();
                                RegisterFile.updateRegister(operands[0], roll);

                            }
                        }));

        instructionList.add(
                new BasicInstruction("loc $s1",
                        "Roll Location : Randomize the location of the active animatronic in $s1",
                        BasicInstructionFormat.I_FORMAT,
                        "000001 fffff 00000 00000 00000 000000",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                Random random = new Random();
                                int roll = random.nextInt(7); // want values 0-6

                                // put random value into $loc
                                int[] operands = statement.getOperands();
                                RegisterFile.updateRegister(operands[0], roll);

                            }
                        }));

        instructionList.add(
                new BasicInstruction("move $s1,-100",
                        "Move location : Move the active animatronic to a specified location represented by an immediate value 0-6 in $s1",
                        BasicInstructionFormat.I_FORMAT,
                        "000101 fffff 00000 ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int value = operands[1] << 16 >> 16;
                                if (value > 6 || value < 0) {
                                    throw new ProcessingException(statement, "Invalid value for location, must be a value 0-6");
                                }
                                RegisterFile.updateRegister(operands[0], value);

                            }
                        }));

        instructionList.add(
                new BasicInstruction("set $s0,-100",
                        "Set animatronic : Set the active animatronic to a specified animatronic represented by an immediate value 0-4 in $s0",
                        BasicInstructionFormat.I_FORMAT,
                        "000111 fffff 00000 ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int value = operands[1] << 16 >> 16;
                                if (value > 4 || value < 0) {
                                    throw new ProcessingException(statement, "Invalid value for animatronic, must be a value 0-4");
                                }
                                RegisterFile.updateRegister(operands[0], value);

                            }
                        }));

        instructionList.add(
                new BasicInstruction("time $s2,-100",
                        "Set time : Set the time to a specified hour represented by an immediate value 0-12 in $s2",
                        BasicInstructionFormat.I_FORMAT,
                        "001001 fffff 00000 ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int value = operands[1] << 16 >> 16;
                                if (value > 12 || value < 0) {
                                    throw new ProcessingException(statement, "Invalid value for time, must be a value 0-12");
                                }
                                RegisterFile.updateRegister(operands[0], value);

                            }
                        }));

        instructionList.add(
                new BasicInstruction("cams $t0, $s0, $s1",
                        "Check Cameras : Prints location, displays camera view, depletes -5 energy",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 fffff sssss ttttt 00000 000100",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                // update energy value
                                int energy = RegisterFile.getValue(operands[0]) - 5;
                                RegisterFile.updateRegister(operands[0], energy);

                                int anim = RegisterFile.getValue(operands[1]);
                                int loc = RegisterFile.getValue(operands[2]);
                                String fullLoc;
                                String imagePath = "";

                                switch (anim) {
                                    case 1:
                                        fullLoc = "Bonnie is in ";
                                        break;
                                    case 2:
                                        fullLoc = "Chica is in ";
                                        break;
                                    case 3:
                                        fullLoc = "Freddy is in ";
                                        break;
                                    case 4:
                                        fullLoc = "Foxy is in ";
                                        break;
                                    default:
                                        fullLoc = "The animatronics seem to be inactive. You are currently checking ";
                                        break;
                                }

                                switch (loc) {
                                    case 1:
                                        fullLoc += "the Kitchen.\n";
                                        imagePath = "kitchen.png";
                                        break;
                                    case 2:
                                        fullLoc += "the Bathroom.\n";
                                        imagePath = "bathroom.png";
                                        break;
                                    case 3:
                                        fullLoc += "Parts & Service.\n";
                                        imagePath = "partsservice.png";
                                        break;
                                    case 4:
                                        fullLoc += "the East Hallway.\n";
                                        imagePath = "east.png";
                                        break;
                                    case 5:
                                        fullLoc += "the West Hallway.\n";
                                        imagePath = "west.png";
                                        break;
                                    case 6:
                                        fullLoc += "Pirate's Cove.\n";
                                        imagePath = "pirates.png";
                                        break;
                                    default:
                                        fullLoc += "the Party Room.\n";
                                        imagePath = "partyroom.png";
                                        break;
                                }

                                SystemIO.printString(fullLoc);

                                // now display camera image
                                try {
                                    java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(
                                            new File(imagePath)
                                    );

                                    if (image != null) {
                                        int baseAddress = 0x10040000;
                                        int displayWidth = 512;
                                        int displayHeight = 256;

                                        int imgWidth = Math.min(image.getWidth(), displayWidth);
                                        int imgHeight = Math.min(image.getHeight(), displayHeight);

                                        for (int y = 0; y < imgHeight; y++) {
                                            for (int x = 0; x < imgWidth; x++) {
                                                int rgb = image.getRGB(x, y);
                                                int address = baseAddress + (y * displayWidth + x) * 4;
                                                Globals.memory.setWord(address, rgb);
                                            }
                                        }
                                        SystemIO.printString("Camera feed displayed!\n");
                                    }
                                } catch (Exception e) {
                                    // just print text if image loading fails
                                    SystemIO.printString("[Camera view unavailable]\n");
                                }
                            }
                        }));

        instructionList.add(
                new BasicInstruction("tick $s2",
                        "Tick : Increase time in $s2 by one hour",
                        BasicInstructionFormat.I_FORMAT,
                        "001001 fffff 00000 0000000000000001",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int value = RegisterFile.getValue(operands[0]) + 1;
                                RegisterFile.updateRegister(operands[0], value);

                            }
                        }));

        instructionList.add(
                new BasicInstruction("use $t0",
                        "Use Power : Decrease power in $t0 by one",
                        BasicInstructionFormat.I_FORMAT,
                        "010001 fffff 00000 0000000000000001",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int value = RegisterFile.getValue(operands[0]) - 1;
                                RegisterFile.updateRegister(operands[0], value);

                            }
                        }));

        instructionList.add(
                new BasicInstruction("boop",
                        "Boop : FNAF Easter Egg",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 001010",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                SystemIO.printString("You booped the poster!\n");
                            }
                        }));

        instructionList.add(
                new BasicInstruction("close $t0, $a1, $a2",
                        "Close Door : Close a door but deplete -10 power from $t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 fffff sssss ttttt 00000 000011",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException {

                                int[] operands = statement.getOperands();

                                // if animatronic is Foxy, deplete 20
                                if (RegisterFile.getValue(operands[1]) == 4) {
                                    RegisterFile.updateRegister(operands[0], RegisterFile.getValue(operands[0]) - 20);
                                }
                                else {
                                    RegisterFile.updateRegister(operands[0], RegisterFile.getValue(operands[0]) - 10);
                                }

                                // update $loc $a0
                                RegisterFile.updateRegister(operands[2], 0);
                                RegisterFile.updateRegister(operands[1], 0);
                                SystemIO.printString("You closed the doors and the animatronics retreated.\n");
                            }
                        }));

        instructionList.add(
                new BasicInstruction("flash $t0, $s0, $s1",
                        "Use Flashlight : Check if an animatronic is at the door, but deplete -2 power from $t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 fffff sssss ttttt 00000 000010",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException {

                                int[] operands = statement.getOperands();
                                RegisterFile.updateRegister(operands[0], RegisterFile.getValue(operands[0]) - 2);

                                int loc = RegisterFile.getValue(operands[2]);
                                int anim = RegisterFile.getValue(operands[1]);
                                String animString;

                                switch (anim) {
                                    case 1:
                                        animString = "Bonnie is ";
                                        break;

                                    case 2:
                                        animString = "Chica is ";
                                        break;

                                    case 3:
                                        animString = "Freddy is ";
                                        break;

                                    case 4:
                                        animString = "Foxy is ";
                                        break;

                                    default:
                                        animString = "";
                                        break;
                                };

                                if (loc == 4 && anim != 0) {
                                    SystemIO.printString(animString + "at the East Doorway!\n");
                                }
                                else if (loc == 5 && anim != 0) {
                                    SystemIO.printString(animString + "at the West Doorway!\n");
                                }
                                else {
                                    SystemIO.printString("Looks like no one is at the door.\n");
                                }
                            }
                        }));

        instructionList.add(
                new BasicInstruction("runto label",
                        "Run! : Jump to statement at label's address unconditionally",
                        BasicInstructionFormat.J_FORMAT,
                        "001100 ffffffffffffffffffffffffff",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                Globals.instructionSet.processJump(
                                        ((RegisterFile.getProgramCounter() & 0xF0000000)
                                                | (operands[0] << 2)));

                            }
                        }));

        instructionList.add(
                new BasicInstruction("equiv $t0, -100, label",
                        "Equivalent (imm) : Jump to statement at label's address if $t0 is equivalent to the immediate value",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "001111 fffff ttttt ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int value = operands[1] << 16 >> 16;

                                if (RegisterFile.getValue(operands[0]) == value)
                                {
                                    Globals.instructionSet.processBranch(operands[2]);
                                }
                            }
                        }));

        instructionList.add(
                new BasicInstruction("equiv $t0, $t1, label",
                        "Equivalent (reg) : Jump to statement at label's address if $t0 is equivalent to $t1",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "010000 fffff ttttt ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();

                                if (RegisterFile.getValue(operands[0]) == RegisterFile.getValue(operands[1]))
                                {
                                    Globals.instructionSet.processBranch(operands[2]);
                                }
                            }
                        }));

        instructionList.add(
                new BasicInstruction("check $t0, $t1, label",
                        "Check time : Jump to statement at label's address if $t0 is less than $t1",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "010111 fffff ttttt ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();

                                if (RegisterFile.getValue(operands[0]) < RegisterFile.getValue(operands[1]))
                                {
                                    Globals.instructionSet.processBranch(operands[2]);
                                }
                            }
                        }));

        instructionList.add(
                new BasicInstruction("addp $t0,-100",
                        "Add Power : Increase the power of $t0 by an immediate value",
                        BasicInstructionFormat.I_FORMAT,
                        "010010 fffff 00000 ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int add1 = RegisterFile.getValue(operands[0]);
                                int add2 = operands[1] << 16 >> 16;
                                int sum = add1 + add2;
                                RegisterFile.updateRegister(operands[0], sum);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("addp $t0, $t1",
                        "Add Power : Increase the power of $t0 by the value in $t1",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 fffff sssss 00000 00000 010011",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int sum = RegisterFile.getValue(operands[0]) + RegisterFile.getValue(operands[1]);
                                RegisterFile.updateRegister(operands[0], sum);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("subp $t0,-100",
                        "Subtract Power : Decrease the power of $t0 by an immediate value",
                        BasicInstructionFormat.I_FORMAT,
                        "010100 fffff 00000 ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int sub1 = RegisterFile.getValue(operands[0]);
                                int sub2 = operands[1] << 16 >> 16;
                                int diff = sub1 - sub2;
                                RegisterFile.updateRegister(operands[0], diff);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("subp $t0, $t1",
                        "Subtract Power : Decrease the power of $t0 by the value in $t1",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 fffff sssss 00000 00000 010101",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int diff = RegisterFile.getValue(operands[0]) - RegisterFile.getValue(operands[1]);
                                RegisterFile.updateRegister(operands[0], diff);
                            }
                        }));

        // Add this instruction to your populate() method in FNAFAssembly.java

        instructionList.add(
                new BasicInstruction("display $s0",
                        "Display : Display image based on animatronic value in $s0",
                        BasicInstructionFormat.I_FORMAT,
                        "011000 fffff 00000 00000 00000 000000",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int animValue = RegisterFile.getValue(operands[0]);

                                // Define image file paths based on animatronic value
                                String imagePath = "";

                                switch (animValue) {
                                    case 1:
                                        imagePath = "bonnie.png";
                                        break;
                                    case 2:
                                        imagePath = "chica.png";
                                        break;
                                    case 3:
                                        imagePath = "office.png";
                                        break;
                                    case 4:
                                        imagePath = "foxy.png";
                                        break;
                                    case 0:
                                        imagePath = "office.png";
                                        break;
                                    default:
                                        SystemIO.printString("Invalid animatronic value. Must be 0-4.\n");
                                        return;
                                }

                                try {
                                    // Load and display image to bitmap display
                                    java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(
                                            new File(imagePath)
                                    );

                                    if (image == null) {
                                        SystemIO.printString("Warning: Could not load image file: " + imagePath + "\n");
                                        return;
                                    }

                                    // Get the bitmap display memory-mapped address (default: 0x10010000)
                                    int baseAddress = 0x10040000;

                                    // Display dimensions (adjust based on your bitmap display settings)
                                    int displayWidth = 512;
                                    int displayHeight = 256;

                                    // Scale image if needed
                                    int imgWidth = Math.min(image.getWidth(), displayWidth);
                                    int imgHeight = Math.min(image.getHeight(), displayHeight);

                                    // Write pixel data to bitmap display memory
                                    for (int y = 0; y < imgHeight; y++) {
                                        for (int x = 0; x < imgWidth; x++) {
                                            int rgb = image.getRGB(x, y);
                                            int address = baseAddress + (y * displayWidth + x) * 4;

                                            try {
                                                Globals.memory.setWord(address, rgb);
                                            } catch (AddressErrorException e) {
                                                throw new ProcessingException(statement,
                                                        "Error writing to bitmap display: " + e.getMessage());
                                            }
                                        }
                                    }


                                } catch (IOException e) {
                                    SystemIO.printString("Error loading image: " + e.getMessage() + "\n");
                                    SystemIO.printString("Make sure image files are in the same directory as your .asm file.\n");
                                } catch (Exception e) {
                                    throw new ProcessingException(statement,
                                            "Unexpected error displaying image: " + e.getMessage());
                                }
                                SystemIO.printString("");
                            }
                        }));

        instructionList.add(
                new BasicInstruction("jumpscare",
                        "Jumpscare : Game over - terminate with message and display animatronic jumpscare",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 111111",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {

                                int animValue = RegisterFile.getValue(16);

                                String imagePath = "";
                                String animName = "";

                                switch (animValue) {
                                    case 1:
                                        imagePath = "bonniejs.png";
                                        animName = "BONNIE";
                                        break;
                                    case 2:
                                        imagePath = "chicajs.png";
                                        animName = "CHICA";
                                        break;
                                    case 3:
                                        imagePath = "freddyjs.png";
                                        animName = "FREDDY";
                                        break;
                                    case 4:
                                        imagePath = "foxyjs.png";
                                        animName = "FOXY";
                                        break;
                                    default:
                                        imagePath = "goldenfreddyjs.png";
                                        animName = "GOLDEN FREDDY";
                                        break;
                                }

                                // Display dramatic jumpscare message
                                SystemIO.printString("\n");
                                SystemIO.printString("═════════════════════════════════════════════════════════════════\n");
                                SystemIO.printString("! " + animName + " jumpscared you and stuffed you into a suit !\n");
                                SystemIO.printString("═════════════════════════════════════════════════════════════════\n");
                                SystemIO.printString("\n                   *** GAME OVER ***\n\n");
                                System.out.flush();

                                try {
                                    // load jumpscare image
                                    java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(
                                            new File(imagePath)
                                    );

                                    if (image == null) {
                                        SystemIO.printString("Warning: Could not load jumpscare image: " + imagePath + "\n");
                                    } else {
                                        // Display to bitmap
                                        int baseAddress = 0x10040000;
                                        int displayWidth = 512;
                                        int displayHeight = 256;

                                        int imgWidth = Math.min(image.getWidth(), displayWidth);
                                        int imgHeight = Math.min(image.getHeight(), displayHeight);

                                        // Write pixel data
                                        for (int y = 0; y < imgHeight; y++) {
                                            for (int x = 0; x < imgWidth; x++) {
                                                int rgb = image.getRGB(x, y);
                                                int address = baseAddress + (y * displayWidth + x) * 4;

                                                try {
                                                    Globals.memory.setWord(address, rgb);
                                                } catch (AddressErrorException e) {
                                                    throw new ProcessingException(statement,
                                                            "Error writing to bitmap display: " + e.getMessage());
                                                }
                                            }
                                        }
                                    }

                                } catch (IOException e) {
                                    SystemIO.printString("Error loading jumpscare: " + e.getMessage() + "\n");
                                } catch (Exception e) {
                                    SystemIO.printString("Jumpscare error: " + e.getMessage() + "\n");
                                }

                                // terminate
                                throw new ProcessingException(statement, "");

                            }
                        }));
        
    }
}
