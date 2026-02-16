# File Operations

> Read, write, and manage files on the system

## Usage

Use this skill to interact with the filesystem - read files, write content, list directories, and manage file operations.

## Syntax

```
TOOL:READ <filepath>
TOOL:WRITE <filepath> <content>
TOOL:LIST <directory>
```

## Examples

```
TOOL:READ config.json
TOOL:WRITE output.txt "Hello, World!"
TOOL:LIST ./data
```

## Capabilities

- Read file contents
- Write to files
- List directory contents
- Check file existence
- Get file metadata

## Safety

File operations are restricted to the working directory and subdirectories. Cannot access system files or parent directories.
