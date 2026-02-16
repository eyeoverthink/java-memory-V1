# Docker Execute

> Run shell commands safely in an isolated Docker container

## Usage

Use this skill to execute potentially dangerous commands in a sandboxed environment. The command runs in an isolated Docker container with no network access, limited resources, and a read-only filesystem.

## Syntax

```
TOOL:DOCKER <command>
```

## Examples

```
TOOL:DOCKER ls -la /
TOOL:DOCKER echo "Hello from sandbox"
TOOL:DOCKER cat /etc/os-release
TOOL:DOCKER python3 -c "print(2**100)"
```

## Safety Features

- **Network Isolation**: No internet access
- **Resource Limits**: 256MB RAM, 0.5 CPU cores
- **Read-Only Filesystem**: Cannot modify host system
- **Timeout**: 5 second execution limit
- **Auto-Cleanup**: Container removed after execution

## When to Use

Use TOOL:DOCKER when:
- Command might be destructive (rm, dd, etc.)
- Testing untrusted code
- Need isolated environment
- Exploring system commands safely

## Limitations

- No persistent storage
- No network access
- Limited execution time
- Alpine Linux environment only
