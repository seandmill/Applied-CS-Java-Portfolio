# SketchClone v1.0

## Description  

SketchClone v1.0 is a local painting application built in JavaFX. It provides users with a simple interface to draw shapes, paint, and create freehand sketches. The application supports basic canvas operations like resizing, saving, and opening image files, making it a functional and lightweight alternative to more complex drawing tools.

---

### Features  

1. **Drawing Tools**:  
   - Draw freehand lines, circles, rectangles, and gradient-filled paint shapes.  
   - Adjustable stroke size and color selection.  

2. **Canvas Operations**:  
   - Resize the canvas dynamically using width and height fields.  
   - Create a new canvas by clearing the existing one.  

3. **File Management**:  
   - Save the canvas as a `.png` image file.  
   - Open existing `.png` files into the canvas.  

4. **User-Friendly Interface**:  
   - Toolbar for file operations (New, Open, Save).  
   - Sidebar for color selection, shape selection, and stroke size adjustment.  

---

### Key Components  

1. **Canvas and GraphicsContext**:  
   - The primary drawing area is a `Canvas` backed by a `GraphicsContext` to handle all painting operations.  

2. **Color Picker and Shape Toggle**:  
   - A `ColorPicker` allows users to select colors, while `ToggleButton` groups enable switching between drawing modes (e.g., circles, lines, rectangles).  

3. **Resizable Canvas**:  
   - Allows users to dynamically resize the drawing area via width and height text fields.  

---

### Tech  

- **Language**: Java (JavaFX)  
- **File I/O**: Save and load `.png` files to and from the local file system.   

---

### Workflow  

1. **Initialization**:  
   - Launches a blank canvas with default dimensions (1000x800 pixels).  

2. **Main Menu Options**:  
   - New Canvas: Clears the existing canvas.  
   - Open: Opens a `.png` image onto the canvas.  
   - Save: Saves the current canvas as a `.png` file.  

3. **Drawing Workflow**:  
   - Select a shape from the sidebar.  
   - Adjust stroke size and choose a color from the color picker.  
   - Use mouse press and drag to draw on the canvas.  

4. **Error Handling**:  
   - Ensures valid canvas dimensions and handles invalid file operations gracefully.  

---

### Complexity Overview  

| Feature                | Time Complexity         |  
|------------------------|--------------------------|  
| Drawing Operations     | O(1) per shape          |  
| Canvas Resize          | O(1)                    |  
| Save Image             | O(n*m)                  |  
| Open Image             | O(n*m)                  |  

*`n` = canvas width, `m` = canvas height.*  

---

### Learning Outcomes  

- **JavaFX Development**: Built a graphical application using JavaFX's `Canvas`, `GraphicsContext`, and layout managers.  
- **File Handling**: Implemented file operations for saving and loading images.  
- **Event-Driven Programming**: Used mouse and keyboard events to create an interactive drawing experience.  
- **UI Design**: Created an intuitive interface with a toolbar and sidebar for streamlined user interaction.  

---

### How to Run  

1. Compile all Java classes.  
2. Run the main class (`sketchclone.java`).  
3. Use the toolbar and sidebar to perform operations and create sketches.  
