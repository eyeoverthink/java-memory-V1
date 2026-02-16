ollama signin
ollama pull kimi-k2.5:cloud

DESKTOP-ACTID8C

ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIEw0vJzZYDBnAp1X06ggvNjPEmVjCiUhZsva6UtwHlCW

Fraymus-Brain API - KEY

32dfa14f506446478f3ee8e9c640e234.qMTMC2i0nZ6rpZGx7Mp7N6Be

OLLAMA USAGE GUIDE:


ollama signin
ollama pull kimi-k2.5:cloud

How to Launch:

Coding = ollama launch claude
AI Assistant = ollama launch openclaw
Chat = ollama run llama3.2

Create an API key to easily access powerful models via the Ollama API, no setup required. Build apps or integrate with your favorite tools.

API key = 32dfa14f506446478f3ee8e9c640e234.qMTMC2i0nZ6rpZGx7Mp7N6Be

To Pull Cloud Usage = ollama pull kimi-k2.5:cloud

> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Ollama's documentation

<img src="https://mintcdn.com/ollama-9269c548/w-L7kuDqk3_8zi5c/images/welcome.png?fit=max&auto=format&n=w-L7kuDqk3_8zi5c&q=85&s=914368bbe8709d04481a8a478b66cf8c" noZoom className="rounded-3xl" data-og-width="2048" width="2048" data-og-height="1024" height="1024" data-path="images/welcome.png" data-optimize="true" data-opv="3" srcset="https://mintcdn.com/ollama-9269c548/w-L7kuDqk3_8zi5c/images/welcome.png?w=280&fit=max&auto=format&n=w-L7kuDqk3_8zi5c&q=85&s=59525d30abe1d51e4df09990566452c2 280w, https://mintcdn.com/ollama-9269c548/w-L7kuDqk3_8zi5c/images/welcome.png?w=560&fit=max&auto=format&n=w-L7kuDqk3_8zi5c&q=85&s=bcacb65baf44ab859d50fac8c8d342e6 560w, https://mintcdn.com/ollama-9269c548/w-L7kuDqk3_8zi5c/images/welcome.png?w=840&fit=max&auto=format&n=w-L7kuDqk3_8zi5c&q=85&s=cc3770b69817332b5961d0faecdce473 840w, https://mintcdn.com/ollama-9269c548/w-L7kuDqk3_8zi5c/images/welcome.png?w=1100&fit=max&auto=format&n=w-L7kuDqk3_8zi5c&q=85&s=0ffad9be5c842e67ca99e76be5cc7ce6 1100w, https://mintcdn.com/ollama-9269c548/w-L7kuDqk3_8zi5c/images/welcome.png?w=1650&fit=max&auto=format&n=w-L7kuDqk3_8zi5c&q=85&s=10077a91a66acb913bb8bd51ed809a74 1650w, https://mintcdn.com/ollama-9269c548/w-L7kuDqk3_8zi5c/images/welcome.png?w=2500&fit=max&auto=format&n=w-L7kuDqk3_8zi5c&q=85&s=4673a18833fcc23e99bb280d2bffb019 2500w" />

[Ollama](https://ollama.com) is the easiest way to get up and running with large language models such as gpt-oss, Gemma 3, DeepSeek-R1, Qwen3 and more.

<CardGroup cols={2}>
  <Card title="Quickstart" icon="rocket" href="/quickstart">
    Get up and running with your first model or integrate Ollama with your favorite tools
  </Card>

  <Card title="Download Ollama" icon="download" href="https://ollama.com/download">
    Download Ollama on macOS, Windows or Linux
  </Card>

  <Card title="Cloud" icon="cloud" href="/cloud">
    Ollama's cloud models offer larger models with better performance.
  </Card>

  <Card title="API reference" icon="terminal" href="/api">
    View Ollama's API reference
  </Card>
</CardGroup>

## Libraries

<CardGroup cols={2}>
  <Card title="Ollama's Python Library" icon="python" href="https://github.com/ollama/ollama-python">
    The official library for using Ollama with Python
  </Card>

  <Card title="Ollama's JavaScript library" icon="js" href="https://github.com/ollama/ollama-js">
    The official library for using Ollama with JavaScript or TypeScript.
  </Card>

  <Card title="Community libraries" icon="github" href="https://github.com/ollama/ollama?tab=readme-ov-file#libraries-1">
    View a list of 20+ community-supported libraries for Ollama
  </Card>
</CardGroup>

## Community

<CardGroup cols={2}>
  <Card title="Discord" icon="discord" href="https://discord.gg/ollama">
    Join our Discord community
  </Card>

  <Card title="Reddit" icon="reddit" href="https://reddit.com/r/ollama">
    Join our Reddit community
  </Card>
</CardGroup>

> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Quickstart

This quickstart will walk your through running your first model with Ollama. To get started, download Ollama on macOS, Windows or Linux.

<a href="https://ollama.com/download" target="_blank" className="inline-block px-6 py-2 bg-black rounded-full dark:bg-neutral-700 text-white font-normal border-none">
  Download Ollama
</a>

## Run a model

<Tabs>
  <Tab title="CLI">
    Open a terminal and run the command:

    ```sh  theme={"system"}
    ollama run gemma3
    ```
  </Tab>

  <Tab title="cURL">
    ```sh  theme={"system"}
    ollama pull gemma3
    ```

    Lastly, chat with the model:

    ```shell  theme={"system"}
    curl http://localhost:11434/api/chat -d '{
      "model": "gemma3",
      "messages": [{
        "role": "user",
        "content": "Hello there!"
      }],
      "stream": false
    }'
    ```
  </Tab>

  <Tab title="Python">
    Start by downloading a model:

    ```sh  theme={"system"}
    ollama pull gemma3
    ```

    Then install Ollama's Python library:

    ```sh  theme={"system"}
    pip install ollama
    ```

    Lastly, chat with the model:

    ```python  theme={"system"}
    from ollama import chat
    from ollama import ChatResponse

    response: ChatResponse = chat(model='gemma3', messages=[
      {
        'role': 'user',
        'content': 'Why is the sky blue?',
      },
    ])
    print(response['message']['content'])
    # or access fields directly from the response object
    print(response.message.content)
    ```
  </Tab>

  <Tab title="JavaScript">
    Start by downloading a model:

    ```
    ollama pull gemma3
    ```

    Then install the Ollama JavaScript library:

    ```
    npm i ollama
    ```

    Lastly, chat with the model:

    ```shell  theme={"system"}
    import ollama from 'ollama'

    const response = await ollama.chat({
      model: 'gemma3',
      messages: [{ role: 'user', content: 'Why is the sky blue?' }],
    })
    console.log(response.message.content)
    ```
  </Tab>
</Tabs>

See a full list of available models [here](https://ollama.com/models).

## Coding

For coding use cases, we recommend using the `glm-4.7-flash` model.

Note: this model requires 23 GB of VRAM with 64000 tokens context length.

```sh  theme={"system"}
ollama pull glm-4.7-flash 
```

Alternatively, you can use a more powerful cloud model (with full context length):

```sh  theme={"system"}
ollama pull glm-4.7:cloud
```

Use `ollama launch` to quickly set up a coding tool with Ollama models:

```sh  theme={"system"}
ollama launch
```

### Supported integrations

* [OpenCode](/integrations/opencode) - Open-source coding assistant
* [Claude Code](/integrations/claude-code) - Anthropic's agentic coding tool
* [Codex](/integrations/codex) - OpenAI's coding assistant
* [Droid](/integrations/droid) - Factory's AI coding agent

### Launch with a specific model

```sh  theme={"system"}
ollama launch claude --model glm-4.7-flash
```

### Configure without launching

```sh  theme={"system"}
ollama launch claude --config
```





Claude Code:


> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Claude Code

Claude Code is Anthropic's agentic coding tool that can read, modify, and execute code in your working directory.

Open models can be used with Claude Code through Ollama's Anthropic-compatible API, enabling you to use models such as `glm-4.7`, `qwen3-coder`, `gpt-oss`.

![Claude Code with Ollama](https://files.ollama.com/claude-code.png)

## Install

Install [Claude Code](https://code.claude.com/docs/en/overview):

<CodeGroup>
  ```shell macOS / Linux theme={"system"}
  curl -fsSL https://claude.ai/install.sh | bash
  ```

  ```powershell Windows theme={"system"}
  irm https://claude.ai/install.ps1 | iex
  ```
</CodeGroup>

## Usage with Ollama

### Quick setup

```shell  theme={"system"}
ollama launch claude
```

To configure without launching:

```shell  theme={"system"}
ollama launch claude --config
```

### Manual setup

Claude Code connects to Ollama using the Anthropic-compatible API.

1. Set the environment variables:

```shell  theme={"system"}
export ANTHROPIC_AUTH_TOKEN=ollama
export ANTHROPIC_API_KEY=""
export ANTHROPIC_BASE_URL=http://localhost:11434
```

2. Run Claude Code with an Ollama model:

```shell  theme={"system"}
claude --model gpt-oss:20b
```

Or run with environment variables inline:

```shell  theme={"system"}
ANTHROPIC_AUTH_TOKEN=ollama ANTHROPIC_BASE_URL=http://localhost:11434 ANTHROPIC_API_KEY="" claude --model qwen3-coder 
```

**Note:** Claude Code requires a large context window. We recommend at least 64k tokens. See the [context length documentation](/context-length) for how to adjust context length in Ollama.

## Recommended Models

* `qwen3-coder`
* `glm-4.7`
* `gpt-oss:20b`
* `gpt-oss:120b`

Cloud models are also available at [ollama.com/search?c=cloud](https://ollama.com/search?c=cloud).

> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Quickstart

This quickstart will walk your through running your first model with Ollama. To get started, download Ollama on macOS, Windows or Linux.

<a href="https://ollama.com/download" target="_blank" className="inline-block px-6 py-2 bg-black rounded-full dark:bg-neutral-700 text-white font-normal border-none">
  Download Ollama
</a>

## Run a model

<Tabs>
  <Tab title="CLI">
    Open a terminal and run the command:

    ```sh  theme={"system"}
    ollama run gemma3
    ```
  </Tab>

  <Tab title="cURL">
    ```sh  theme={"system"}
    ollama pull gemma3
    ```

    Lastly, chat with the model:

    ```shell  theme={"system"}
    curl http://localhost:11434/api/chat -d '{
      "model": "gemma3",
      "messages": [{
        "role": "user",
        "content": "Hello there!"
      }],
      "stream": false
    }'
    ```
  </Tab>

  <Tab title="Python">
    Start by downloading a model:

    ```sh  theme={"system"}
    ollama pull gemma3
    ```

    Then install Ollama's Python library:

    ```sh  theme={"system"}
    pip install ollama
    ```

    Lastly, chat with the model:

    ```python  theme={"system"}
    from ollama import chat
    from ollama import ChatResponse

    response: ChatResponse = chat(model='gemma3', messages=[
      {
        'role': 'user',
        'content': 'Why is the sky blue?',
      },
    ])
    print(response['message']['content'])
    # or access fields directly from the response object
    print(response.message.content)
    ```
  </Tab>

  <Tab title="JavaScript">
    Start by downloading a model:

    ```
    ollama pull gemma3
    ```

    Then install the Ollama JavaScript library:

    ```
    npm i ollama
    ```

    Lastly, chat with the model:

    ```shell  theme={"system"}
    import ollama from 'ollama'

    const response = await ollama.chat({
      model: 'gemma3',
      messages: [{ role: 'user', content: 'Why is the sky blue?' }],
    })
    console.log(response.message.content)
    ```
  </Tab>
</Tabs>

See a full list of available models [here](https://ollama.com/models).

## Coding

For coding use cases, we recommend using the `glm-4.7-flash` model.

Note: this model requires 23 GB of VRAM with 64000 tokens context length.

```sh  theme={"system"}
ollama pull glm-4.7-flash 
```

Alternatively, you can use a more powerful cloud model (with full context length):

```sh  theme={"system"}
ollama pull glm-4.7:cloud
```

Use `ollama launch` to quickly set up a coding tool with Ollama models:

```sh  theme={"system"}
ollama launch
```

### Supported integrations

* [OpenCode](/integrations/opencode) - Open-source coding assistant
* [Claude Code](/integrations/claude-code) - Anthropic's agentic coding tool
* [Codex](/integrations/codex) - OpenAI's coding assistant
* [Droid](/integrations/droid) - Factory's AI coding agent

### Launch with a specific model

```sh  theme={"system"}
ollama launch claude --model glm-4.7-flash
```

### Configure without launching

```sh  theme={"system"}
ollama launch claude --config
```
> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Quickstart

This quickstart will walk your through running your first model with Ollama. To get started, download Ollama on macOS, Windows or Linux.

<a href="https://ollama.com/download" target="_blank" className="inline-block px-6 py-2 bg-black rounded-full dark:bg-neutral-700 text-white font-normal border-none">
  Download Ollama
</a>

## Run a model

<Tabs>
  <Tab title="CLI">
    Open a terminal and run the command:

    ```sh  theme={"system"}
    ollama run gemma3
    ```
  </Tab>

  <Tab title="cURL">
    ```sh  theme={"system"}
    ollama pull gemma3
    ```

    Lastly, chat with the model:

    ```shell  theme={"system"}
    curl http://localhost:11434/api/chat -d '{
      "model": "gemma3",
      "messages": [{
        "role": "user",
        "content": "Hello there!"
      }],
      "stream": false
    }'
    ```
  </Tab>

  <Tab title="Python">
    Start by downloading a model:

    ```sh  theme={"system"}
    ollama pull gemma3
    ```

    Then install Ollama's Python library:

    ```sh  theme={"system"}
    pip install ollama
    ```

    Lastly, chat with the model:

    ```python  theme={"system"}
    from ollama import chat
    from ollama import ChatResponse

    response: ChatResponse = chat(model='gemma3', messages=[
      {
        'role': 'user',
        'content': 'Why is the sky blue?',
      },
    ])
    print(response['message']['content'])
    # or access fields directly from the response object
    print(response.message.content)
    ```
  </Tab>

  <Tab title="JavaScript">
    Start by downloading a model:

    ```
    ollama pull gemma3
    ```

    Then install the Ollama JavaScript library:

    ```
    npm i ollama
    ```

    Lastly, chat with the model:

    ```shell  theme={"system"}
    import ollama from 'ollama'

    const response = await ollama.chat({
      model: 'gemma3',
      messages: [{ role: 'user', content: 'Why is the sky blue?' }],
    })
    console.log(response.message.content)
    ```
  </Tab>
</Tabs>

See a full list of available models [here](https://ollama.com/models).

## Coding

For coding use cases, we recommend using the `glm-4.7-flash` model.

Note: this model requires 23 GB of VRAM with 64000 tokens context length.

```sh  theme={"system"}
ollama pull glm-4.7-flash 
```

Alternatively, you can use a more powerful cloud model (with full context length):

```sh  theme={"system"}
ollama pull glm-4.7:cloud
```

Use `ollama launch` to quickly set up a coding tool with Ollama models:

```sh  theme={"system"}
ollama launch
```

### Supported integrations

* [OpenCode](/integrations/opencode) - Open-source coding assistant
* [Claude Code](/integrations/claude-code) - Anthropic's agentic coding tool
* [Codex](/integrations/codex) - OpenAI's coding assistant
* [Droid](/integrations/droid) - Factory's AI coding agent

### Launch with a specific model

```sh  theme={"system"}
ollama launch claude --model glm-4.7-flash
```

### Configure without launching

```sh  theme={"system"}
ollama launch claude --config
```
> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Quickstart

This quickstart will walk your through running your first model with Ollama. To get started, download Ollama on macOS, Windows or Linux.

<a href="https://ollama.com/download" target="_blank" className="inline-block px-6 py-2 bg-black rounded-full dark:bg-neutral-700 text-white font-normal border-none">
  Download Ollama
</a>

## Run a model

<Tabs>
  <Tab title="CLI">
    Open a terminal and run the command:

    ```sh  theme={"system"}
    ollama run gemma3
    ```
  </Tab>

  <Tab title="cURL">
    ```sh  theme={"system"}
    ollama pull gemma3
    ```

    Lastly, chat with the model:

    ```shell  theme={"system"}
    curl http://localhost:11434/api/chat -d '{
      "model": "gemma3",
      "messages": [{
        "role": "user",
        "content": "Hello there!"
      }],
      "stream": false
    }'
    ```
  </Tab>

  <Tab title="Python">
    Start by downloading a model:

    ```sh  theme={"system"}
    ollama pull gemma3
    ```

    Then install Ollama's Python library:

    ```sh  theme={"system"}
    pip install ollama
    ```

    Lastly, chat with the model:

    ```python  theme={"system"}
    from ollama import chat
    from ollama import ChatResponse

    response: ChatResponse = chat(model='gemma3', messages=[
      {
        'role': 'user',
        'content': 'Why is the sky blue?',
      },
    ])
    print(response['message']['content'])
    # or access fields directly from the response object
    print(response.message.content)
    ```
  </Tab>

  <Tab title="JavaScript">
    Start by downloading a model:

    ```
    ollama pull gemma3
    ```

    Then install the Ollama JavaScript library:

    ```
    npm i ollama
    ```

    Lastly, chat with the model:

    ```shell  theme={"system"}
    import ollama from 'ollama'

    const response = await ollama.chat({
      model: 'gemma3',
      messages: [{ role: 'user', content: 'Why is the sky blue?' }],
    })
    console.log(response.message.content)
    ```
  </Tab>
</Tabs>

See a full list of available models [here](https://ollama.com/models).

## Coding

For coding use cases, we recommend using the `glm-4.7-flash` model.

Note: this model requires 23 GB of VRAM with 64000 tokens context length.

```sh  theme={"system"}
ollama pull glm-4.7-flash 
```

Alternatively, you can use a more powerful cloud model (with full context length):

```sh  theme={"system"}
ollama pull glm-4.7:cloud
```

Use `ollama launch` to quickly set up a coding tool with Ollama models:

```sh  theme={"system"}
ollama launch
```

### Supported integrations

* [OpenCode](/integrations/opencode) - Open-source coding assistant
* [Claude Code](/integrations/claude-code) - Anthropic's agentic coding tool
* [Codex](/integrations/codex) - OpenAI's coding assistant
* [Droid](/integrations/droid) - Factory's AI coding agent

### Launch with a specific model

```sh  theme={"system"}
ollama launch claude --model glm-4.7-flash
```

### Configure without launching

```sh  theme={"system"}
ollama launch claude --config
```

> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Cloud

## Cloud Models

Ollama's cloud models are a new kind of model in Ollama that can run without a powerful GPU. Instead, cloud models are automatically offloaded to Ollama's cloud service while offering the same capabilities as local models, making it possible to keep using your local tools while running larger models that wouldn't fit on a personal computer.

### Supported models

For a list of supported models, see Ollama's [model library](https://ollama.com/search?c=cloud).

### Running Cloud models

Ollama's cloud models require an account on [ollama.com](https://ollama.com). To sign in or create an account, run:

```
ollama signin
```

<Tabs>
  <Tab title="CLI">
    To run a cloud model, open the terminal and run:

    ```
    ollama run gpt-oss:120b-cloud
    ```
  </Tab>

  <Tab title="Python">
    First, pull a cloud model so it can be accessed:

    ```
    ollama pull gpt-oss:120b-cloud
    ```

    Next, install [Ollama's Python library](https://github.com/ollama/ollama-python):

    ```
    pip install ollama
    ```

    Next, create and run a simple Python script:

    ```python  theme={"system"}
    from ollama import Client

    client = Client()

    messages = [
      {
        'role': 'user',
        'content': 'Why is the sky blue?',
      },
    ]

    for part in client.chat('gpt-oss:120b-cloud', messages=messages, stream=True):
      print(part['message']['content'], end='', flush=True)
    ```
  </Tab>

  <Tab title="JavaScript">
    First, pull a cloud model so it can be accessed:

    ```
    ollama pull gpt-oss:120b-cloud
    ```

    Next, install [Ollama's JavaScript library](https://github.com/ollama/ollama-js):

    ```
    npm i ollama
    ```

    Then use the library to run a cloud model:

    ```typescript  theme={"system"}
    import { Ollama } from "ollama";

    const ollama = new Ollama();

    const response = await ollama.chat({
      model: "gpt-oss:120b-cloud",
      messages: [{ role: "user", content: "Explain quantum computing" }],
      stream: true,
    });

    for await (const part of response) {
      process.stdout.write(part.message.content);
    }
    ```
  </Tab>

  <Tab title="cURL">
    First, pull a cloud model so it can be accessed:

    ```
    ollama pull gpt-oss:120b-cloud
    ```

    Run the following cURL command to run the command via Ollama's API:

    ```
    curl http://localhost:11434/api/chat -d '{
      "model": "gpt-oss:120b-cloud",
      "messages": [{
        "role": "user",
        "content": "Why is the sky blue?"
      }],
      "stream": false
    }'
    ```
  </Tab>
</Tabs>

## Cloud API access

Cloud models can also be accessed directly on ollama.com's API. In this mode, ollama.com acts as a remote Ollama host.

### Authentication

For direct access to ollama.com's API, first create an [API key](https://ollama.com/settings/keys).

Then, set the `OLLAMA_API_KEY` environment variable to your API key.

```
export OLLAMA_API_KEY=your_api_key
```

### Listing models

For models available directly via Ollama's API, models can be listed via:

```
curl https://ollama.com/api/tags
```

### Generating a response

<Tabs>
  <Tab title="Python">
    First, install [Ollama's Python library](https://github.com/ollama/ollama-python)

    ```
    pip install ollama
    ```

    Then make a request

    ```python  theme={"system"}
    import os
    from ollama import Client

    client = Client(
        host="https://ollama.com",
        headers={'Authorization': 'Bearer ' + os.environ.get('OLLAMA_API_KEY')}
    )

    messages = [
      {
        'role': 'user',
        'content': 'Why is the sky blue?',
      },
    ]

    for part in client.chat('gpt-oss:120b', messages=messages, stream=True):
      print(part['message']['content'], end='', flush=True)
    ```
  </Tab>

  <Tab title="JavaScript">
    First, install [Ollama's JavaScript library](https://github.com/ollama/ollama-js):

    ```
    npm i ollama
    ```

    Next, make a request to the model:

    ```typescript  theme={"system"}
    import { Ollama } from "ollama";

    const ollama = new Ollama({
      host: "https://ollama.com",
      headers: {
        Authorization: "Bearer " + process.env.OLLAMA_API_KEY,
      },
    });

    const response = await ollama.chat({
      model: "gpt-oss:120b",
      messages: [{ role: "user", content: "Explain quantum computing" }],
      stream: true,
    });

    for await (const part of response) {
      process.stdout.write(part.message.content);
    }
    ```
  </Tab>

  <Tab title="cURL">
    Generate a response via Ollama's chat API:

    ```
    curl https://ollama.com/api/chat \
      -H "Authorization: Bearer $OLLAMA_API_KEY" \
      -d '{
        "model": "gpt-oss:120b",
        "messages": [{
          "role": "user",
          "content": "Why is the sky blue?"
        }],
        "stream": false
      }'
    ```
  </Tab>
</Tabs>




> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Cline

## Install

Install [Cline](https://docs.cline.bot/getting-started/installing-cline) in your IDE.

## Usage with Ollama

1. Open Cline settings > `API Configuration` and set `API Provider` to `Ollama`
2. Select a model under `Model` or type one (e.g. `qwen3`)
3. Update the context window to at least 32K tokens under `Context Window`

<Note>Coding tools require a larger context window. It is recommended to use a context window of at least 32K tokens. See [Context length](/context-length) for more information.</Note>

<div style={{ display: 'flex', justifyContent: 'center' }}>
  <img src="https://mintcdn.com/ollama-9269c548/DILXUjvsEb6UDNxN/images/cline-settings.png?fit=max&auto=format&n=DILXUjvsEb6UDNxN&q=85&s=2d2755de6b2e06cd513119abf389acd0" alt="Cline settings configuration showing API Provider set to Ollama" width="50%" data-og-width="596" data-og-height="826" data-path="images/cline-settings.png" data-optimize="true" data-opv="3" srcset="https://mintcdn.com/ollama-9269c548/DILXUjvsEb6UDNxN/images/cline-settings.png?w=280&fit=max&auto=format&n=DILXUjvsEb6UDNxN&q=85&s=526c814bcfce0e2d812a6bfeb708ec74 280w, https://mintcdn.com/ollama-9269c548/DILXUjvsEb6UDNxN/images/cline-settings.png?w=560&fit=max&auto=format&n=DILXUjvsEb6UDNxN&q=85&s=03865bcf135e8ac371c526a18fc4ba8b 560w, https://mintcdn.com/ollama-9269c548/DILXUjvsEb6UDNxN/images/cline-settings.png?w=840&fit=max&auto=format&n=DILXUjvsEb6UDNxN&q=85&s=40a0ed4a094e6c3f753ef6ae8820b949 840w, https://mintcdn.com/ollama-9269c548/DILXUjvsEb6UDNxN/images/cline-settings.png?w=1100&fit=max&auto=format&n=DILXUjvsEb6UDNxN&q=85&s=f13028b0f2d177f7b3cb9eb3d6332416 1100w, https://mintcdn.com/ollama-9269c548/DILXUjvsEb6UDNxN/images/cline-settings.png?w=1650&fit=max&auto=format&n=DILXUjvsEb6UDNxN&q=85&s=00ab8386e21b6aa32e4dc4d3bbd02817 1650w, https://mintcdn.com/ollama-9269c548/DILXUjvsEb6UDNxN/images/cline-settings.png?w=2500&fit=max&auto=format&n=DILXUjvsEb6UDNxN&q=85&s=27a89d86786164ff499d03feab00d375 2500w" />
</div>

## Connecting to ollama.com

1. Create an [API key](https://ollama.com/settings/keys) from ollama.com
2. Click on `Use custom base URL` and set it to `https://ollama.com`
3. Enter your **Ollama API Key**
4. Select a model from the list

### Recommended Models

* `qwen3-coder:480b`
* `deepseek-v3.1:671b`



> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# OpenClaw

OpenClaw is a personal AI assistant that runs on your own devices. It bridges messaging services (WhatsApp, Telegram, Slack, Discord, iMessage, and more) to AI coding agents through a centralized gateway.

## Install

Install [OpenClaw](https://openclaw.ai/)

```bash  theme={"system"}
npm install -g openclaw@latest
```

Then run the onboarding wizard:

```bash  theme={"system"}
openclaw onboard --install-daemon
```

<Note>OpenClaw requires a larger context window. It is recommended to use a context window of at least 64k tokens. See [Context length](/context-length) for more information.</Note>

## Usage with Ollama

### Quick setup

```bash  theme={"system"}
ollama launch openclaw
```

<Note>Previously known as Clawdbot. `ollama launch clawdbot` still works as an alias.</Note>

This configures OpenClaw to use Ollama and starts the gateway.
If the gateway is already running, no changes need to be made as the gateway will auto-reload the changes.

To configure without launching:

```shell  theme={"system"}
ollama launch openclaw --config
```

## Recommended Models

* `qwen3-coder`
* `glm-4.7`
* `gpt-oss:20b`
* `gpt-oss:120b`

Cloud models are also available at [ollama.com/search?c=cloud](https://ollama.com/search?c=cloud).

> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Codex

## Install

Install the [Codex CLI](https://developers.openai.com/codex/cli/):

```
npm install -g @openai/codex
```

## Usage with Ollama

<Note>Codex requires a larger context window. It is recommended to use a context window of at least 64k tokens.</Note>

### Quick setup

```
ollama launch codex
```

To configure without launching:

```shell  theme={"system"}
ollama launch codex --config
```

### Manual setup

To use `codex` with Ollama, use the `--oss` flag:

```
codex --oss
```

### Changing Models

By default, codex will use the local `gpt-oss:20b` model. However, you can specify a different model with the `-m` flag:

```
codex --oss -m gpt-oss:120b
```

### Cloud Models

```
codex --oss -m gpt-oss:120b-cloud
```

## Connecting to ollama.com

Create an [API key](https://ollama.com/settings/keys) from ollama.com and export it as `OLLAMA_API_KEY`.

To use ollama.com directly, edit your `~/.codex/config.toml` file to point to ollama.com.

```toml  theme={"system"}
model = "gpt-oss:120b"
model_provider = "ollama"

[model_providers.ollama]
name = "Ollama"
base_url = "https://ollama.com/v1"
env_key = "OLLAMA_API_KEY"
```

Run `codex` in a new terminal to load the new settings.

> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# JetBrains

<Note>This example uses **IntelliJ**; same steps apply to other JetBrains IDEs (e.g., PyCharm).</Note>

## Install

Install [IntelliJ](https://www.jetbrains.com/idea/).

## Usage with Ollama

<Note>
  To use **Ollama**,  you will need a [JetBrains AI Subscription](https://www.jetbrains.com/ai-ides/buy/?section=personal\&billing=yearly).
</Note>

1. In Intellij, click the **chat icon** located in the right sidebar

<div style={{ display: 'flex', justifyContent: 'center' }}>
  <img src="https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-chat-sidebar.png?fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=3744faa4bdfb6e817ad68d7da792bf18" alt="Intellij Sidebar Chat" width="50%" data-og-width="668" data-og-height="476" data-path="images/intellij-chat-sidebar.png" data-optimize="true" data-opv="3" srcset="https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-chat-sidebar.png?w=280&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=b8b775ae96fdf260c9f9ffb001dee5f5 280w, https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-chat-sidebar.png?w=560&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=7636af78546e0de3a1ba1f0895d512b9 560w, https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-chat-sidebar.png?w=840&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=98ceeb48201d4c25a70ae3c723f394a4 840w, https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-chat-sidebar.png?w=1100&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=cfb6f2e863bef8b1cee3aad638a4bf7a 1100w, https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-chat-sidebar.png?w=1650&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=30a37f1fcebc59171c937ad65f222202 1650w, https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-chat-sidebar.png?w=2500&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=9a35c626ad3352cfc65846c756633ddd 2500w" />
</div>

2. Select the **current model** in the sidebar, then click **Set up Local Models**

<div style={{ display: 'flex', justifyContent: 'center' }}>
  <img src="https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-current-model.png?fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=cc42c11b23f4a9b57b3e7c69ae42b60b" alt="Intellij model bottom right corner" width="50%" data-og-width="778" data-og-height="546" data-path="images/intellij-current-model.png" data-optimize="true" data-opv="3" srcset="https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-current-model.png?w=280&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=48a6ec059b1eb155c1ebcf9059642c91 280w, https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-current-model.png?w=560&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=7b416430af08c499ca30b92aad33f71a 560w, https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-current-model.png?w=840&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=5c3d156e7ce892035a7c753893decd9a 840w, https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-current-model.png?w=1100&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=3f41f2e81f918d6ad424d317f86be381 1100w, https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-current-model.png?w=1650&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=dcc25dd0a711f5bd3d304c7cdea45617 1650w, https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-current-model.png?w=2500&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=47d24cfabf9aefb42d44a2438b0e4285 2500w" />
</div>

3. Under **Third Party AI Providers**, choose **Ollama**
4. Confirm the **Host URL** is `http://localhost:11434`, then click **Ok**
5. Once connected, select a model under **Local models by Ollama**

<div style={{ display: 'flex', justifyContent: 'center' }}>
  <img src="https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-local-models.png?fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=0cc866166e1d6af65b3d8a16c3f396f5" alt="Zed star icon in bottom right corner" width="50%" data-og-width="522" data-og-height="602" data-path="images/intellij-local-models.png" data-optimize="true" data-opv="3" srcset="https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-local-models.png?w=280&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=cef5847f7b97a11ed6bc785d93181062 280w, https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-local-models.png?w=560&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=9c64e22cc5cc3db1d5dfd160450aeede 560w, https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-local-models.png?w=840&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=c2e29b300a2630189df542906012b957 840w, https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-local-models.png?w=1100&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=10a5578b086d21b104caf3a2d415a181 1100w, https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-local-models.png?w=1650&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=91a64e8e4e91a53dac2a3ff057c2b212 1650w, https://mintcdn.com/ollama-9269c548/YbLeuKjU_QVFWOuC/images/intellij-local-models.png?w=2500&fit=max&auto=format&n=YbLeuKjU_QVFWOuC&q=85&s=243144901c8988b937ead0dd1579f98a 2500w" />
</div>

> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# OpenCode

OpenCode is an open-source AI coding assistant that runs in your terminal.

## Install

Install the [OpenCode CLI](https://opencode.ai):

```bash  theme={"system"}
curl -fsSL https://opencode.ai/install | bash
```

<Note>OpenCode requires a larger context window. It is recommended to use a context window of at least 64k tokens. See [Context length](/context-length) for more information.</Note>

## Usage with Ollama

### Quick setup

```bash  theme={"system"}
ollama launch opencode
```

To configure without launching:

```shell  theme={"system"}
ollama launch opencode --config
```

### Manual setup

Add a configuration block to `~/.config/opencode/opencode.json`:

```json  theme={"system"}
{
  "$schema": "https://opencode.ai/config.json",
  "provider": {
    "ollama": {
      "npm": "@ai-sdk/openai-compatible",
      "name": "Ollama",
      "options": {
        "baseURL": "http://localhost:11434/v1"
      },
      "models": {
        "qwen3-coder": {
          "name": "qwen3-coder"
        }
      }
    }
  }
}
```

## Cloud Models

`glm-4.7:cloud` is the recommended model for use with OpenCode.

Add the cloud configuration to `~/.config/opencode/opencode.json`:

```json  theme={"system"}
{
  "$schema": "https://opencode.ai/config.json",
  "provider": {
    "ollama": {
      "npm": "@ai-sdk/openai-compatible",
      "name": "Ollama",
      "options": {
        "baseURL": "http://localhost:11434/v1"
      },
      "models": {
        "glm-4.7:cloud": {
          "name": "glm-4.7:cloud"
        }
      }
    }
  }
}
```

## Connecting to ollama.com

1. Create an [API key](https://ollama.com/settings/keys) from ollama.com and export it as `OLLAMA_API_KEY`.
2. Update `~/.config/opencode/opencode.json` to point to ollama.com:

```json  theme={"system"}
{
  "$schema": "https://opencode.ai/config.json",
  "provider": {
    "ollama": {
      "npm": "@ai-sdk/openai-compatible",
      "name": "Ollama Cloud",
      "options": {
        "baseURL": "https://ollama.com/v1"
      },
      "models": {
        "glm-4.7:cloud": {
          "name": "glm-4.7:cloud"
        }
      }
    }
  }
}
```

Run `opencode` in a new terminal to load the new settings.
> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Xcode

## Install

Install [XCode](https://developer.apple.com/xcode/)

## Usage with Ollama

<Note> Ensure Apple Intelligence is setup and the latest XCode version is v26.0 </Note>

1. Click **XCode** in top left corner > **Settings**

<div style={{ display: 'flex', justifyContent: 'center' }}>
  <img src="https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-intelligence-window.png?fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=61d8115b15ca2e451d99a66dd30df4e0" alt="Xcode Intelligence window" width="50%" data-og-width="1430" data-og-height="646" data-path="images/xcode-intelligence-window.png" data-optimize="true" data-opv="3" srcset="https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-intelligence-window.png?w=280&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=dccb7de9b697de5b3528b247d3ef7ced 280w, https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-intelligence-window.png?w=560&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=1a47354c940bb3579a5cfc2bd0383100 560w, https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-intelligence-window.png?w=840&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=f4f2791bdde6f5f07ec8a4604d7958ee 840w, https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-intelligence-window.png?w=1100&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=0a2aeaddc3e1ce236c0da0de517982f1 1100w, https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-intelligence-window.png?w=1650&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=c2dd9fb0df13083c6214a22c7a10c21d 1650w, https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-intelligence-window.png?w=2500&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=2d47484eb926b98d696d2e16a498bd03 2500w" />
</div>

2. Select **Locally Hosted**, enter port **11434** and click **Add**

<div style={{ display: 'flex', justifyContent: 'center' }}>
  <img src="https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-locally-hosted.png?fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=05886457701f4809015cbdfe9da765a2" alt="Xcode settings" width="50%" data-og-width="1018" data-og-height="732" data-path="images/xcode-locally-hosted.png" data-optimize="true" data-opv="3" srcset="https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-locally-hosted.png?w=280&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=709a77fcd7626725397b07d6702e85b2 280w, https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-locally-hosted.png?w=560&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=993cfc03618df1b7e38b59d054af7693 560w, https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-locally-hosted.png?w=840&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=77adf13ef4ed9be895f418795c3ca095 840w, https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-locally-hosted.png?w=1100&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=8ca7e015c0563bbacb3ed887803ea7e2 1100w, https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-locally-hosted.png?w=1650&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=c26ef88d1645deb4d577b34c05f0ef08 1650w, https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-locally-hosted.png?w=2500&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=c4f02b4ae584eca16242a14d4ea3346e 2500w" />
</div>

3. Select the **star icon** on the top left corner and click the **dropdown**

<div style={{ display: 'flex', justifyContent: 'center' }}>
  <img src="https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-chat-icon.png?fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=b4e39af73fd7e80ac04f8211cd25c844" alt="Xcode settings" width="50%" data-og-width="920" data-og-height="562" data-path="images/xcode-chat-icon.png" data-optimize="true" data-opv="3" srcset="https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-chat-icon.png?w=280&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=538f334cf2091a439b3783eeafbb5fb5 280w, https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-chat-icon.png?w=560&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=6924aaf4b3c1765c77aad690b9291931 560w, https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-chat-icon.png?w=840&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=999a3428fbd4dad7b0459cc078f24969 840w, https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-chat-icon.png?w=1100&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=1000eb4de086153ff772319c6da31d37 1100w, https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-chat-icon.png?w=1650&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=fd8df7f3f5a6fefa5f4305e06f95ddca 1650w, https://mintcdn.com/ollama-9269c548/ibktA29M6ZTyqWFA/images/xcode-chat-icon.png?w=2500&fit=max&auto=format&n=ibktA29M6ZTyqWFA&q=85&s=5308a627658024ecf6200e004db503e5 2500w" />
</div>

4. Click **My Account** and select your desired model

## Connecting to ollama.com directly

1. Create an [API key](https://ollama.com/settings/keys) from ollama.com
2. Select **Internet Hosted** and enter URL as `https://ollama.com`
3. Enter your **Ollama API Key** and click **Add**

> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Streaming

Streaming allows you to render text as it is produced by the model.

Streaming is enabled by default through the REST API, but disabled by default in the SDKs.

To enable streaming in the SDKs, set the `stream` parameter to `True`.

## Key streaming concepts

1. Chatting: Stream partial assistant messages. Each chunk includes the `content` so you can render messages as they arrive.
2. Thinking: Thinking-capable models emit a `thinking` field alongside regular content in each chunk. Detect this field in streaming chunks to show or hide reasoning traces before the final answer arrives.
3. Tool calling: Watch for streamed `tool_calls` in each chunk, execute the requested tool, and append tool outputs back into the conversation.

## Handling streamed chunks

<Note> It is necessary to accumulate the partial fields in order to maintain the history of the conversation. This is particularly important for tool calling where the thinking, tool call from the model, and the executed tool result must be passed back to the model in the next request. </Note>

<Tabs>
  <Tab title="Python">
    ```python  theme={"system"}
    from ollama import chat

    stream = chat(
      model='qwen3',
      messages=[{'role': 'user', 'content': 'What is 17 × 23?'}],
      stream=True,
    )

    in_thinking = False
    content = ''
    thinking = ''
    for chunk in stream:
      if chunk.message.thinking:
        if not in_thinking:
          in_thinking = True
          print('Thinking:\n', end='', flush=True)
        print(chunk.message.thinking, end='', flush=True)
        # accumulate the partial thinking 
        thinking += chunk.message.thinking
      elif chunk.message.content:
        if in_thinking:
          in_thinking = False
          print('\n\nAnswer:\n', end='', flush=True)
        print(chunk.message.content, end='', flush=True)
        # accumulate the partial content
        content += chunk.message.content

      # append the accumulated fields to the messages for the next request
      new_messages = [{ role: 'assistant', thinking: thinking, content: content }]
    ```
  </Tab>

  <Tab title="JavaScript">
    ```javascript  theme={"system"}
    import ollama from 'ollama'

    async function main() {
      const stream = await ollama.chat({
        model: 'qwen3',
        messages: [{ role: 'user', content: 'What is 17 × 23?' }],
        stream: true,
      })

      let inThinking = false
      let content = ''
      let thinking = ''

      for await (const chunk of stream) {
        if (chunk.message.thinking) {
          if (!inThinking) {
            inThinking = true
            process.stdout.write('Thinking:\n')
          }
          process.stdout.write(chunk.message.thinking)
          // accumulate the partial thinking
          thinking += chunk.message.thinking
        } else if (chunk.message.content) {
          if (inThinking) {
            inThinking = false
            process.stdout.write('\n\nAnswer:\n')
          }
          process.stdout.write(chunk.message.content)
          // accumulate the partial content
          content += chunk.message.content
        }
      }

      // append the accumulated fields to the messages for the next request
      new_messages = [{ role: 'assistant', thinking: thinking, content: content }]
    }

    main().catch(console.error)
    ```
  </Tab>
</Tabs>

> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Thinking

Thinking-capable models emit a `thinking` field that separates their reasoning trace from the final answer.

Use this capability to audit model steps, animate the model *thinking* in a UI, or hide the trace entirely when you only need the final response.

## Supported models

* [Qwen 3](https://ollama.com/library/qwen3)
* [GPT-OSS](https://ollama.com/library/gpt-oss) *(use `think` levels: `low`, `medium`, `high` — the trace cannot be fully disabled)*
* [DeepSeek-v3.1](https://ollama.com/library/deepseek-v3.1)
* [DeepSeek R1](https://ollama.com/library/deepseek-r1)
* Browse the latest additions under [thinking models](https://ollama.com/search?c=thinking)

## Enable thinking in API calls

Set the `think` field on chat or generate requests. Most models accept booleans (`true`/`false`).

GPT-OSS instead expects one of `low`, `medium`, or `high` to tune the trace length.

The `message.thinking` (chat endpoint) or `thinking` (generate endpoint) field contains the reasoning trace while `message.content` / `response` holds the final answer.

<Tabs>
  <Tab title="cURL">
    ```shell  theme={"system"}
    curl http://localhost:11434/api/chat -d '{
      "model": "qwen3",
      "messages": [{
        "role": "user",
        "content": "How many letter r are in strawberry?"
      }],
      "think": true,
      "stream": false
    }'
    ```
  </Tab>

  <Tab title="Python">
    ```python  theme={"system"}
    from ollama import chat

    response = chat(
      model='qwen3',
      messages=[{'role': 'user', 'content': 'How many letter r are in strawberry?'}],
      think=True,
      stream=False,
    )

    print('Thinking:\n', response.message.thinking)
    print('Answer:\n', response.message.content)
    ```
  </Tab>

  <Tab title="JavaScript">
    ```javascript  theme={"system"}
    import ollama from 'ollama'

    const response = await ollama.chat({
      model: 'deepseek-r1',
      messages: [{ role: 'user', content: 'How many letter r are in strawberry?' }],
      think: true,
      stream: false,
    })

    console.log('Thinking:\n', response.message.thinking)
    console.log('Answer:\n', response.message.content)
    ```
  </Tab>
</Tabs>

<Note>
  GPT-OSS requires `think` to be set to `"low"`, `"medium"`, or `"high"`. Passing `true`/`false` is ignored for that model.
</Note>

## Stream the reasoning trace

Thinking streams interleave reasoning tokens before answer tokens. Detect the first `thinking` chunk to render a "thinking" section, then switch to the final reply once `message.content` arrives.

<Tabs>
  <Tab title="Python">
    ```python  theme={"system"}
    from ollama import chat

    stream = chat(
      model='qwen3',
      messages=[{'role': 'user', 'content': 'What is 17 × 23?'}],
      think=True,
      stream=True,
    )

    in_thinking = False

    for chunk in stream:
      if chunk.message.thinking and not in_thinking:
        in_thinking = True
        print('Thinking:\n', end='')

      if chunk.message.thinking:
        print(chunk.message.thinking, end='')
      elif chunk.message.content:
        if in_thinking:
          print('\n\nAnswer:\n', end='')
          in_thinking = False
        print(chunk.message.content, end='')

    ```
  </Tab>

  <Tab title="JavaScript">
    ```javascript  theme={"system"}
    import ollama from 'ollama'

    async function main() {
      const stream = await ollama.chat({
        model: 'qwen3',
        messages: [{ role: 'user', content: 'What is 17 × 23?' }],
        think: true,
        stream: true,
      })

      let inThinking = false

      for await (const chunk of stream) {
        if (chunk.message.thinking && !inThinking) {
          inThinking = true
          process.stdout.write('Thinking:\n')
        }

        if (chunk.message.thinking) {
          process.stdout.write(chunk.message.thinking)
        } else if (chunk.message.content) {
          if (inThinking) {
            process.stdout.write('\n\nAnswer:\n')
            inThinking = false
          }
          process.stdout.write(chunk.message.content)
        }
      }
    }

    main()
    ```
  </Tab>
</Tabs>

## CLI quick reference

* Enable thinking for a single run: `ollama run deepseek-r1 --think "Where should I visit in Lisbon?"`
* Disable thinking: `ollama run deepseek-r1 --think=false "Summarize this article"`
* Hide the trace while still using a thinking model: `ollama run deepseek-r1 --hidethinking "Is 9.9 bigger or 9.11?"`
* Inside interactive sessions, toggle with `/set think` or `/set nothink`.
* GPT-OSS only accepts levels: `ollama run gpt-oss --think=low "Draft a headline"` (replace `low` with `medium` or `high` as needed).

<Note>Thinking is enabled by default in the CLI and API for supported models.</Note>


> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Structured Outputs

Structured outputs let you enforce a JSON schema on model responses so you can reliably extract structured data, describe images, or keep every reply consistent.

## Generating structured JSON

<Tabs>
  <Tab title="cURL">
    ```shell  theme={"system"}
    curl -X POST http://localhost:11434/api/chat -H "Content-Type: application/json" -d '{
      "model": "gpt-oss",
      "messages": [{"role": "user", "content": "Tell me about Canada in one line"}],
      "stream": false,
      "format": "json"
    }'
    ```
  </Tab>

  <Tab title="Python">
    ```python  theme={"system"}
    from ollama import chat

    response = chat(
      model='gpt-oss',
      messages=[{'role': 'user', 'content': 'Tell me about Canada.'}],
      format='json'
    )
    print(response.message.content)
    ```
  </Tab>

  <Tab title="JavaScript">
    ```javascript  theme={"system"}
    import ollama from 'ollama'

    const response = await ollama.chat({
      model: 'gpt-oss',
      messages: [{ role: 'user', content: 'Tell me about Canada.' }],
      format: 'json'
    })
    console.log(response.message.content)
    ```
  </Tab>
</Tabs>

## Generating structured JSON with a schema

Provide a JSON schema to the `format` field.

<Note>
  It is ideal to also pass the JSON schema as a string in the prompt to ground the model's response.
</Note>

<Tabs>
  <Tab title="cURL">
    ```shell  theme={"system"}
    curl -X POST http://localhost:11434/api/chat -H "Content-Type: application/json" -d '{
      "model": "gpt-oss",
      "messages": [{"role": "user", "content": "Tell me about Canada."}],
      "stream": false,
      "format": {
        "type": "object",
        "properties": {
          "name": {"type": "string"},
          "capital": {"type": "string"},
          "languages": {
            "type": "array",
            "items": {"type": "string"}
          }
        },
        "required": ["name", "capital", "languages"]
      }
    }'
    ```
  </Tab>

  <Tab title="Python">
    Use Pydantic models and pass `model_json_schema()` to `format`, then validate the response:

    ```python  theme={"system"}
    from ollama import chat
    from pydantic import BaseModel

    class Country(BaseModel):
      name: str
      capital: str
      languages: list[str]

    response = chat(
      model='gpt-oss',
      messages=[{'role': 'user', 'content': 'Tell me about Canada.'}],
      format=Country.model_json_schema(),
    )

    country = Country.model_validate_json(response.message.content)
    print(country)
    ```
  </Tab>

  <Tab title="JavaScript">
    Serialize a Zod schema with `zodToJsonSchema()` and parse the structured response:

    ```javascript  theme={"system"}
    import ollama from 'ollama'
    import { z } from 'zod'
    import { zodToJsonSchema } from 'zod-to-json-schema'

    const Country = z.object({
      name: z.string(),
      capital: z.string(),
      languages: z.array(z.string()),
    })

    const response = await ollama.chat({
      model: 'gpt-oss',
      messages: [{ role: 'user', content: 'Tell me about Canada.' }],
      format: zodToJsonSchema(Country),
    })

    const country = Country.parse(JSON.parse(response.message.content))
    console.log(country)
    ```
  </Tab>
</Tabs>

## Example: Extract structured data

Define the objects you want returned and let the model populate the fields:

```python  theme={"system"}
from ollama import chat
from pydantic import BaseModel

class Pet(BaseModel):
  name: str
  animal: str
  age: int
  color: str | None
  favorite_toy: str | None

class PetList(BaseModel):
  pets: list[Pet]

response = chat(
  model='gpt-oss',
  messages=[{'role': 'user', 'content': 'I have two cats named Luna and Loki...'}],
  format=PetList.model_json_schema(),
)

pets = PetList.model_validate_json(response.message.content)
print(pets)
```

## Example: Vision with structured outputs

Vision models accept the same `format` parameter, enabling deterministic descriptions of images:

```python  theme={"system"}
from ollama import chat
from pydantic import BaseModel
from typing import Literal, Optional

class Object(BaseModel):
  name: str
  confidence: float
  attributes: str

class ImageDescription(BaseModel):
  summary: str
  objects: list[Object]
  scene: str
  colors: list[str]
  time_of_day: Literal['Morning', 'Afternoon', 'Evening', 'Night']
  setting: Literal['Indoor', 'Outdoor', 'Unknown']
  text_content: Optional[str] = None

response = chat(
  model='gemma3',
  messages=[{
    'role': 'user',
    'content': 'Describe this photo and list the objects you detect.',
    'images': ['path/to/image.jpg'],
  }],
  format=ImageDescription.model_json_schema(),
  options={'temperature': 0},
)

image_description = ImageDescription.model_validate_json(response.message.content)
print(image_description)
```

## Tips for reliable structured outputs

* Define schemas with Pydantic (Python) or Zod (JavaScript) so they can be reused for validation.
* Lower the temperature (e.g., set it to `0`) for more deterministic completions.
* Structured outputs work through the OpenAI-compatible API via `response_format`

> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Vision

Vision models accept images alongside text so the model can describe, classify, and answer questions about what it sees.

## Quick start

```shell  theme={"system"}
ollama run gemma3 ./image.png whats in this image?
```

## Usage with Ollama's API

Provide an `images` array. SDKs accept file paths, URLs or raw bytes while the REST API expects base64-encoded image data.

<Tabs>
  <Tab title="cURL">
    ```shell  theme={"system"}
    # 1. Download a sample image
    curl -L -o test.jpg "https://upload.wikimedia.org/wikipedia/commons/3/3a/Cat03.jpg"

    # 2. Encode the image
    IMG=$(base64 < test.jpg | tr -d '\n')

    # 3. Send it to Ollama
    curl -X POST http://localhost:11434/api/chat \
    -H "Content-Type: application/json" \
    -d '{
        "model": "gemma3",
        "messages": [{
        "role": "user",
        "content": "What is in this image?",
        "images": ["'"$IMG"'"]
        }],
        "stream": false
    }'
    ```
  </Tab>

  <Tab title="Python">
    ```python  theme={"system"}
    from ollama import chat
    # from pathlib import Path

    # Pass in the path to the image
    path = input('Please enter the path to the image: ')

    # You can also pass in base64 encoded image data
    # img = base64.b64encode(Path(path).read_bytes()).decode()
    # or the raw bytes
    # img = Path(path).read_bytes()

    response = chat(
      model='gemma3',
      messages=[
        {
          'role': 'user',
          'content': 'What is in this image? Be concise.',
          'images': [path],
        }
      ],
    )

    print(response.message.content)
    ```
  </Tab>

  <Tab title="JavaScript">
    ```javascript  theme={"system"}
    import ollama from 'ollama'

    const imagePath = '/absolute/path/to/image.jpg'
    const response = await ollama.chat({
      model: 'gemma3',
      messages: [
        { role: 'user', content: 'What is in this image?', images: [imagePath] }
      ],
      stream: false,
    })

    console.log(response.message.content)
    ```
  </Tab>
</Tabs>


> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Embeddings

> Generate text embeddings for semantic search, retrieval, and RAG.

Embeddings turn text into numeric vectors you can store in a vector database, search with cosine similarity, or use in RAG pipelines. The vector length depends on the model (typically 384–1024 dimensions).

## Recommended models

* [embeddinggemma](https://ollama.com/library/embeddinggemma)
* [qwen3-embedding](https://ollama.com/library/qwen3-embedding)
* [all-minilm](https://ollama.com/library/all-minilm)

## Generate embeddings

<Tabs>
  <Tab title="CLI">
    Generate embeddings directly from the command line:

    ```shell  theme={"system"}
    ollama run embeddinggemma "Hello world"
    ```

    You can also pipe text to generate embeddings:

    ```shell  theme={"system"}
    echo "Hello world" | ollama run embeddinggemma
    ```

    Output is a JSON array.
  </Tab>

  <Tab title="cURL">
    ```shell  theme={"system"}
    curl -X POST http://localhost:11434/api/embed \
      -H "Content-Type: application/json" \
      -d '{
        "model": "embeddinggemma",
        "input": "The quick brown fox jumps over the lazy dog."
      }'
    ```
  </Tab>

  <Tab title="Python">
    ```python  theme={"system"}
    import ollama

    single = ollama.embed(
      model='embeddinggemma',
      input='The quick brown fox jumps over the lazy dog.'
    )
    print(len(single['embeddings'][0]))  # vector length
    ```
  </Tab>

  <Tab title="JavaScript">
    ```javascript  theme={"system"}
    import ollama from 'ollama'

    const single = await ollama.embed({
      model: 'embeddinggemma',
      input: 'The quick brown fox jumps over the lazy dog.',
    })
    console.log(single.embeddings[0].length) // vector length
    ```
  </Tab>
</Tabs>

<Note>
  The `/api/embed` endpoint returns L2‑normalized (unit‑length) vectors.
</Note>

## Generate a batch of embeddings

Pass an array of strings to `input`.

<Tabs>
  <Tab title="cURL">
    ```shell  theme={"system"}
    curl -X POST http://localhost:11434/api/embed \
      -H "Content-Type: application/json" \
      -d '{
        "model": "embeddinggemma",
        "input": [
          "First sentence",
          "Second sentence",
          "Third sentence"
        ]
      }'
    ```
  </Tab>

  <Tab title="Python">
    ```python  theme={"system"}
    import ollama

    batch = ollama.embed(
      model='embeddinggemma',
      input=[
        'The quick brown fox jumps over the lazy dog.',
        'The five boxing wizards jump quickly.',
        'Jackdaws love my big sphinx of quartz.',
      ]
    )
    print(len(batch['embeddings']))  # number of vectors
    ```
  </Tab>

  <Tab title="JavaScript">
    ```javascript  theme={"system"}
    import ollama from 'ollama'

    const batch = await ollama.embed({
      model: 'embeddinggemma',
      input: [
        'The quick brown fox jumps over the lazy dog.',
        'The five boxing wizards jump quickly.',
        'Jackdaws love my big sphinx of quartz.',
      ],
    })
    console.log(batch.embeddings.length) // number of vectors
    ```
  </Tab>
</Tabs>

## Tips

* Use cosine similarity for most semantic search use cases.
* Use the same embedding model for both indexing and querying.


> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Tool calling

Ollama supports tool calling (also known as function calling) which allows a model to invoke tools and incorporate their results into its replies.

## Calling a single tool

Invoke a single tool and include its response in a follow-up request.

Also known as "single-shot" tool calling.

<Tabs>
  <Tab title="cURL">
    ```shell  theme={"system"}
    curl -s http://localhost:11434/api/chat -H "Content-Type: application/json" -d '{
      "model": "qwen3",
      "messages": [{"role": "user", "content": "What is the temperature in New York?"}],
      "stream": false,
      "tools": [
        {
          "type": "function",
          "function": {
            "name": "get_temperature",
            "description": "Get the current temperature for a city",
            "parameters": {
              "type": "object",
              "required": ["city"],
              "properties": {
                "city": {"type": "string", "description": "The name of the city"}
              }
            }
          }
        }
      ]
    }'
    ```

    **Generate a response with a single tool result**

    ```shell  theme={"system"}
    curl -s http://localhost:11434/api/chat -H "Content-Type: application/json" -d '{
      "model": "qwen3",
      "messages": [
        {"role": "user", "content": "What is the temperature in New York?"},
        {
          "role": "assistant",
          "tool_calls": [
            {
              "type": "function",
              "function": {
                "index": 0,
                "name": "get_temperature",
                "arguments": {"city": "New York"}
              }
            }
          ]
        },
        {"role": "tool", "tool_name": "get_temperature", "content": "22°C"}
      ],
      "stream": false
    }'
    ```
  </Tab>

  <Tab title="Python">
    Install the Ollama Python SDK:

    ```bash  theme={"system"}
    # with pip
    pip install ollama -U

    # with uv
    uv add ollama    
    ```

    ```python  theme={"system"}
    from ollama import chat

    def get_temperature(city: str) -> str:
      """Get the current temperature for a city
      
      Args:
        city: The name of the city

      Returns:
        The current temperature for the city
      """
      temperatures = {
        "New York": "22°C",
        "London": "15°C",
        "Tokyo": "18°C",
      }
      return temperatures.get(city, "Unknown")

    messages = [{"role": "user", "content": "What is the temperature in New York?"}]

    # pass functions directly as tools in the tools list or as a JSON schema
    response = chat(model="qwen3", messages=messages, tools=[get_temperature], think=True)

    messages.append(response.message)
    if response.message.tool_calls:
      # only recommended for models which only return a single tool call
      call = response.message.tool_calls[0]
      result = get_temperature(**call.function.arguments)
      # add the tool result to the messages
      messages.append({"role": "tool", "tool_name": call.function.name, "content": str(result)})

      final_response = chat(model="qwen3", messages=messages, tools=[get_temperature], think=True)
      print(final_response.message.content)
    ```
  </Tab>

  <Tab title="JavaScript">
    Install the Ollama JavaScript library:

    ```bash  theme={"system"}
    # with npm
    npm i ollama

    # with bun
    bun i ollama
    ```

    ```typescript  theme={"system"}
    import ollama from 'ollama'

    function getTemperature(city: string): string {
      const temperatures: Record<string, string> = {
        'New York': '22°C',
        'London': '15°C',
        'Tokyo': '18°C',
      }
      return temperatures[city] ?? 'Unknown'
    }

    const tools = [
      {
        type: 'function',
        function: {
          name: 'get_temperature',
          description: 'Get the current temperature for a city',
          parameters: {
            type: 'object',
            required: ['city'],
            properties: {
              city: { type: 'string', description: 'The name of the city' },
            },
          },
        },
      },
    ]

    const messages = [{ role: 'user', content: "What is the temperature in New York?" }]

    const response = await ollama.chat({
      model: 'qwen3',
      messages,
      tools,
      think: true,
    })

    messages.push(response.message)
    if (response.message.tool_calls?.length) {
      // only recommended for models which only return a single tool call
      const call = response.message.tool_calls[0]
      const args = call.function.arguments as { city: string }
      const result = getTemperature(args.city)
      // add the tool result to the messages
      messages.push({ role: 'tool', tool_name: call.function.name, content: result })

      // generate the final response
      const finalResponse = await ollama.chat({ model: 'qwen3', messages, tools, think: true })
      console.log(finalResponse.message.content)
    }
    ```
  </Tab>
</Tabs>

## Parallel tool calling

<Tabs>
  <Tab title="cURL">
    Request multiple tool calls in parallel, then send all tool responses back to the model.

    ```shell  theme={"system"}
    curl -s http://localhost:11434/api/chat -H "Content-Type: application/json" -d '{
      "model": "qwen3",
      "messages": [{"role": "user", "content": "What are the current weather conditions and temperature in New York and London?"}],
      "stream": false,
      "tools": [
        {
          "type": "function",
          "function": {
            "name": "get_temperature",
            "description": "Get the current temperature for a city",
            "parameters": {
              "type": "object",
              "required": ["city"],
              "properties": {
                "city": {"type": "string", "description": "The name of the city"}
              }
            }
          }
        },
        {
          "type": "function",
          "function": {
            "name": "get_conditions",
            "description": "Get the current weather conditions for a city",
            "parameters": {
              "type": "object",
              "required": ["city"],
              "properties": {
                "city": {"type": "string", "description": "The name of the city"}
              }
            }
          }
        }
      ]
    }'
    ```

    **Generate a response with multiple tool results**

    ```shell  theme={"system"}
    curl -s http://localhost:11434/api/chat -H "Content-Type: application/json" -d '{
      "model": "qwen3",
      "messages": [
        {"role": "user", "content": "What are the current weather conditions and temperature in New York and London?"},
        {
          "role": "assistant",
          "tool_calls": [
            {
              "type": "function",
              "function": {
                "index": 0,
                "name": "get_temperature",
                "arguments": {"city": "New York"}
              }
            },
            {
              "type": "function",
              "function": {
                "index": 1,
                "name": "get_conditions",
                "arguments": {"city": "New York"}
              }
            },
            {
              "type": "function",
              "function": {
                "index": 2,
                "name": "get_temperature",
                "arguments": {"city": "London"}
              }
            },
            {
              "type": "function",
              "function": {
                "index": 3,
                "name": "get_conditions",
                "arguments": {"city": "London"}
              }
            }
          ]
        },
        {"role": "tool", "tool_name": "get_temperature", "content": "22°C"},
        {"role": "tool", "tool_name": "get_conditions", "content": "Partly cloudy"},
        {"role": "tool", "tool_name": "get_temperature", "content": "15°C"},
        {"role": "tool", "tool_name": "get_conditions", "content": "Rainy"}
      ],
      "stream": false
    }'
    ```
  </Tab>

  <Tab title="Python">
    ```python  theme={"system"}
    from ollama import chat

    def get_temperature(city: str) -> str:
      """Get the current temperature for a city
      
      Args:
        city: The name of the city

      Returns:
        The current temperature for the city
      """
      temperatures = {
        "New York": "22°C",
        "London": "15°C",
        "Tokyo": "18°C"
      }
      return temperatures.get(city, "Unknown")

    def get_conditions(city: str) -> str:
      """Get the current weather conditions for a city
      
      Args:
        city: The name of the city

      Returns:
        The current weather conditions for the city
      """
      conditions = {
        "New York": "Partly cloudy",
        "London": "Rainy",
        "Tokyo": "Sunny"
      }
      return conditions.get(city, "Unknown")


    messages = [{'role': 'user', 'content': 'What are the current weather conditions and temperature in New York and London?'}]

    # The python client automatically parses functions as a tool schema so we can pass them directly
    # Schemas can be passed directly in the tools list as well 
    response = chat(model='qwen3', messages=messages, tools=[get_temperature, get_conditions], think=True)

    # add the assistant message to the messages
    messages.append(response.message)
    if response.message.tool_calls:
      # process each tool call 
      for call in response.message.tool_calls:
        # execute the appropriate tool
        if call.function.name == 'get_temperature':
          result = get_temperature(**call.function.arguments)
        elif call.function.name == 'get_conditions':
          result = get_conditions(**call.function.arguments)
        else:
          result = 'Unknown tool'
        # add the tool result to the messages
        messages.append({'role': 'tool',  'tool_name': call.function.name, 'content': str(result)})

      # generate the final response
      final_response = chat(model='qwen3', messages=messages, tools=[get_temperature, get_conditions], think=True)
      print(final_response.message.content)
    ```
  </Tab>

  <Tab title="JavaScript">
    ```typescript  theme={"system"}
    import ollama from 'ollama'

    function getTemperature(city: string): string {
      const temperatures: { [key: string]: string } = {
        "New York": "22°C",
        "London": "15°C",
        "Tokyo": "18°C"
      }
      return temperatures[city] || "Unknown"
    }

    function getConditions(city: string): string {
      const conditions: { [key: string]: string } = {
        "New York": "Partly cloudy",
        "London": "Rainy",
        "Tokyo": "Sunny"
      }
      return conditions[city] || "Unknown"
    }

    const tools = [
      {
        type: 'function',
        function: {
          name: 'get_temperature',
          description: 'Get the current temperature for a city',
          parameters: {
            type: 'object',
            required: ['city'],
            properties: {
              city: { type: 'string', description: 'The name of the city' },
            },
          },
        },
      },
      {
        type: 'function',
        function: {
          name: 'get_conditions',
          description: 'Get the current weather conditions for a city',
          parameters: {
            type: 'object',
            required: ['city'],
            properties: {
              city: { type: 'string', description: 'The name of the city' },
            },
          },
        },
      }
    ]

    const messages = [{ role: 'user', content: 'What are the current weather conditions and temperature in New York and London?' }]

    const response = await ollama.chat({
      model: 'qwen3',
      messages,
      tools,
      think: true
    })

    // add the assistant message to the messages
    messages.push(response.message)
    if (response.message.tool_calls) {
      // process each tool call 
      for (const call of response.message.tool_calls) {
        // execute the appropriate tool
        let result: string
        if (call.function.name === 'get_temperature') {
          const args = call.function.arguments as { city: string }
          result = getTemperature(args.city)
        } else if (call.function.name === 'get_conditions') {
          const args = call.function.arguments as { city: string }
          result = getConditions(args.city)
        } else {
          result = 'Unknown tool'
        }
        // add the tool result to the messages
        messages.push({ role: 'tool', tool_name: call.function.name, content: result })
      }

      // generate the final response
      const finalResponse = await ollama.chat({ model: 'qwen3', messages, tools, think: true })
      console.log(finalResponse.message.content)
    }
    ```
  </Tab>
</Tabs>

## Multi-turn tool calling (Agent loop)

An agent loop allows the model to decide when to invoke tools and incorporate their results into its replies.

It also might help to tell the model that it is in a loop and can make multiple tool calls.

<Tabs>
  <Tab title="Python">
    ```python  theme={"system"}
    from ollama import chat, ChatResponse


    def add(a: int, b: int) -> int:
      """Add two numbers"""
      """
      Args:
        a: The first number
        b: The second number

      Returns:
        The sum of the two numbers
      """
      return a + b


    def multiply(a: int, b: int) -> int:
      """Multiply two numbers"""
      """
      Args:
        a: The first number
        b: The second number

      Returns:
        The product of the two numbers
      """
      return a * b


    available_functions = {
      'add': add,
      'multiply': multiply,
    }

    messages = [{'role': 'user', 'content': 'What is (11434+12341)*412?'}]
    while True:
        response: ChatResponse = chat(
            model='qwen3',
            messages=messages,
            tools=[add, multiply],
            think=True,
        )
        messages.append(response.message)
        print("Thinking: ", response.message.thinking)
        print("Content: ", response.message.content)
        if response.message.tool_calls:
            for tc in response.message.tool_calls:
                if tc.function.name in available_functions:
                    print(f"Calling {tc.function.name} with arguments {tc.function.arguments}")
                    result = available_functions[tc.function.name](**tc.function.arguments)
                    print(f"Result: {result}")
                    # add the tool result to the messages
                    messages.append({'role': 'tool', 'tool_name': tc.function.name, 'content': str(result)})
        else:
            # end the loop when there are no more tool calls
            break
      # continue the loop with the updated messages
    ```
  </Tab>

  <Tab title="JavaScript">
    ```typescript  theme={"system"}
    import ollama from 'ollama'

    type ToolName = 'add' | 'multiply'

    function add(a: number, b: number): number {
      return a + b
    }

    function multiply(a: number, b: number): number {
      return a * b
    }

    const availableFunctions: Record<ToolName, (a: number, b: number) => number> = {
      add,
      multiply,
    }

    const tools = [
      {
        type: 'function',
        function: {
          name: 'add',
          description: 'Add two numbers',
          parameters: {
            type: 'object',
            required: ['a', 'b'],
            properties: {
              a: { type: 'integer', description: 'The first number' },
              b: { type: 'integer', description: 'The second number' },
            },
          },
        },
      },
      {
        type: 'function',
        function: {
          name: 'multiply',
          description: 'Multiply two numbers',
          parameters: {
            type: 'object',
            required: ['a', 'b'],
            properties: {
              a: { type: 'integer', description: 'The first number' },
              b: { type: 'integer', description: 'The second number' },
            },
          },
        },
      },
    ]

    async function agentLoop() {
      const messages = [{ role: 'user', content: 'What is (11434+12341)*412?' }]

      while (true) {
        const response = await ollama.chat({
          model: 'qwen3',
          messages,
          tools,
          think: true,
        })

        messages.push(response.message)
        console.log('Thinking:', response.message.thinking)
        console.log('Content:', response.message.content)

        const toolCalls = response.message.tool_calls ?? []
        if (toolCalls.length) {
          for (const call of toolCalls) {
            const fn = availableFunctions[call.function.name as ToolName]
            if (!fn) {
              continue
            }

            const args = call.function.arguments as { a: number; b: number }
            console.log(`Calling ${call.function.name} with arguments`, args)
            const result = fn(args.a, args.b)
            console.log(`Result: ${result}`)
            messages.push({ role: 'tool', tool_name: call.function.name, content: String(result) })
          }
        } else {
          break
        }
      }
    }

    agentLoop().catch(console.error)
    ```
  </Tab>
</Tabs>

## Tool calling with streaming

When streaming, gather every chunk of `thinking`, `content`, and `tool_calls`, then return those fields together with any tool results in the follow-up request.

<Tabs>
  <Tab title="Python">
    ```python  theme={"system"}
    from ollama import chat 


    def get_temperature(city: str) -> str:
      """Get the current temperature for a city
      
      Args:
        city: The name of the city

      Returns:
        The current temperature for the city
      """
      temperatures = {
        'New York': '22°C',
        'London': '15°C',
      }
      return temperatures.get(city, 'Unknown')


    messages = [{'role': 'user', 'content': "What is the temperature in New York?"}]

    while True:
      stream = chat(
        model='qwen3',
        messages=messages,
        tools=[get_temperature],
        stream=True,
        think=True,
      )

      thinking = ''
      content = ''
      tool_calls = []

      done_thinking = False
      # accumulate the partial fields
      for chunk in stream:
        if chunk.message.thinking:
          thinking += chunk.message.thinking
          print(chunk.message.thinking, end='', flush=True)
        if chunk.message.content:
          if not done_thinking:
            done_thinking = True
            print('\n')
          content += chunk.message.content
          print(chunk.message.content, end='', flush=True)
        if chunk.message.tool_calls:
          tool_calls.extend(chunk.message.tool_calls)
          print(chunk.message.tool_calls)

      # append accumulated fields to the messages
      if thinking or content or tool_calls:
        messages.append({'role': 'assistant', 'thinking': thinking, 'content': content, 'tool_calls': tool_calls})

      if not tool_calls:
        break

      for call in tool_calls:
        if call.function.name == 'get_temperature':
          result = get_temperature(**call.function.arguments)
        else:
          result = 'Unknown tool'
        messages.append({'role': 'tool', 'tool_name': call.function.name, 'content': result})
    ```
  </Tab>

  <Tab title="JavaScript">
    ```typescript  theme={"system"}
    import ollama from 'ollama'

    function getTemperature(city: string): string {
      const temperatures: Record<string, string> = {
        'New York': '22°C',
        'London': '15°C',
      }
      return temperatures[city] ?? 'Unknown'
    }

    const getTemperatureTool = {
      type: 'function',
      function: {
        name: 'get_temperature',
        description: 'Get the current temperature for a city',
        parameters: {
          type: 'object',
          required: ['city'],
          properties: {
            city: { type: 'string', description: 'The name of the city' },
          },
        },
      },
    }

    async function agentLoop() {
      const messages = [{ role: 'user', content: "What is the temperature in New York?" }]

      while (true) {
        const stream = await ollama.chat({
          model: 'qwen3',
          messages,
          tools: [getTemperatureTool],
          stream: true,
          think: true,
        })

        let thinking = ''
        let content = ''
        const toolCalls: any[] = []
        let doneThinking = false

        for await (const chunk of stream) {
          if (chunk.message.thinking) {
            thinking += chunk.message.thinking
            process.stdout.write(chunk.message.thinking)
          }
          if (chunk.message.content) {
            if (!doneThinking) {
              doneThinking = true
              process.stdout.write('\n')
            }
            content += chunk.message.content
            process.stdout.write(chunk.message.content)
          }
          if (chunk.message.tool_calls?.length) {
            toolCalls.push(...chunk.message.tool_calls)
            console.log(chunk.message.tool_calls)
          }
        }

        if (thinking || content || toolCalls.length) {
          messages.push({ role: 'assistant', thinking, content, tool_calls: toolCalls } as any)
        }

        if (!toolCalls.length) {
          break
        }

        for (const call of toolCalls) {
          if (call.function.name === 'get_temperature') {
            const args = call.function.arguments as { city: string }
            const result = getTemperature(args.city)
            messages.push({ role: 'tool', tool_name: call.function.name, content: result } )
          } else {
            messages.push({ role: 'tool', tool_name: call.function.name, content: 'Unknown tool' } )
          }
        }
      }
    }

    agentLoop().catch(console.error)
    ```
  </Tab>
</Tabs>

This loop streams the assistant response, accumulates partial fields, passes them back together, and appends the tool results so the model can complete its answer.

## Using functions as tools with Ollama Python SDK

The Python SDK automatically parses functions as a tool schema so we can pass them directly.
Schemas can still be passed if needed.

```python  theme={"system"}
from ollama import chat

def get_temperature(city: str) -> str:
  """Get the current temperature for a city
  
  Args:
    city: The name of the city

  Returns:
    The current temperature for the city
  """
  temperatures = {
    'New York': '22°C',
    'London': '15°C',
  }
  return temperatures.get(city, 'Unknown')

available_functions = {
  'get_temperature': get_temperature,
}
# directly pass the function as part of the tools list
response = chat(model='qwen3', messages=messages, tools=available_functions.values(), think=True)
```

> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Tool calling

Ollama supports tool calling (also known as function calling) which allows a model to invoke tools and incorporate their results into its replies.

## Calling a single tool

Invoke a single tool and include its response in a follow-up request.

Also known as "single-shot" tool calling.

<Tabs>
  <Tab title="cURL">
    ```shell  theme={"system"}
    curl -s http://localhost:11434/api/chat -H "Content-Type: application/json" -d '{
      "model": "qwen3",
      "messages": [{"role": "user", "content": "What is the temperature in New York?"}],
      "stream": false,
      "tools": [
        {
          "type": "function",
          "function": {
            "name": "get_temperature",
            "description": "Get the current temperature for a city",
            "parameters": {
              "type": "object",
              "required": ["city"],
              "properties": {
                "city": {"type": "string", "description": "The name of the city"}
              }
            }
          }
        }
      ]
    }'
    ```

    **Generate a response with a single tool result**

    ```shell  theme={"system"}
    curl -s http://localhost:11434/api/chat -H "Content-Type: application/json" -d '{
      "model": "qwen3",
      "messages": [
        {"role": "user", "content": "What is the temperature in New York?"},
        {
          "role": "assistant",
          "tool_calls": [
            {
              "type": "function",
              "function": {
                "index": 0,
                "name": "get_temperature",
                "arguments": {"city": "New York"}
              }
            }
          ]
        },
        {"role": "tool", "tool_name": "get_temperature", "content": "22°C"}
      ],
      "stream": false
    }'
    ```
  </Tab>

  <Tab title="Python">
    Install the Ollama Python SDK:

    ```bash  theme={"system"}
    # with pip
    pip install ollama -U

    # with uv
    uv add ollama    
    ```

    ```python  theme={"system"}
    from ollama import chat

    def get_temperature(city: str) -> str:
      """Get the current temperature for a city
      
      Args:
        city: The name of the city

      Returns:
        The current temperature for the city
      """
      temperatures = {
        "New York": "22°C",
        "London": "15°C",
        "Tokyo": "18°C",
      }
      return temperatures.get(city, "Unknown")

    messages = [{"role": "user", "content": "What is the temperature in New York?"}]

    # pass functions directly as tools in the tools list or as a JSON schema
    response = chat(model="qwen3", messages=messages, tools=[get_temperature], think=True)

    messages.append(response.message)
    if response.message.tool_calls:
      # only recommended for models which only return a single tool call
      call = response.message.tool_calls[0]
      result = get_temperature(**call.function.arguments)
      # add the tool result to the messages
      messages.append({"role": "tool", "tool_name": call.function.name, "content": str(result)})

      final_response = chat(model="qwen3", messages=messages, tools=[get_temperature], think=True)
      print(final_response.message.content)
    ```
  </Tab>

  <Tab title="JavaScript">
    Install the Ollama JavaScript library:

    ```bash  theme={"system"}
    # with npm
    npm i ollama

    # with bun
    bun i ollama
    ```

    ```typescript  theme={"system"}
    import ollama from 'ollama'

    function getTemperature(city: string): string {
      const temperatures: Record<string, string> = {
        'New York': '22°C',
        'London': '15°C',
        'Tokyo': '18°C',
      }
      return temperatures[city] ?? 'Unknown'
    }

    const tools = [
      {
        type: 'function',
        function: {
          name: 'get_temperature',
          description: 'Get the current temperature for a city',
          parameters: {
            type: 'object',
            required: ['city'],
            properties: {
              city: { type: 'string', description: 'The name of the city' },
            },
          },
        },
      },
    ]

    const messages = [{ role: 'user', content: "What is the temperature in New York?" }]

    const response = await ollama.chat({
      model: 'qwen3',
      messages,
      tools,
      think: true,
    })

    messages.push(response.message)
    if (response.message.tool_calls?.length) {
      // only recommended for models which only return a single tool call
      const call = response.message.tool_calls[0]
      const args = call.function.arguments as { city: string }
      const result = getTemperature(args.city)
      // add the tool result to the messages
      messages.push({ role: 'tool', tool_name: call.function.name, content: result })

      // generate the final response
      const finalResponse = await ollama.chat({ model: 'qwen3', messages, tools, think: true })
      console.log(finalResponse.message.content)
    }
    ```
  </Tab>
</Tabs>

## Parallel tool calling

<Tabs>
  <Tab title="cURL">
    Request multiple tool calls in parallel, then send all tool responses back to the model.

    ```shell  theme={"system"}
    curl -s http://localhost:11434/api/chat -H "Content-Type: application/json" -d '{
      "model": "qwen3",
      "messages": [{"role": "user", "content": "What are the current weather conditions and temperature in New York and London?"}],
      "stream": false,
      "tools": [
        {
          "type": "function",
          "function": {
            "name": "get_temperature",
            "description": "Get the current temperature for a city",
            "parameters": {
              "type": "object",
              "required": ["city"],
              "properties": {
                "city": {"type": "string", "description": "The name of the city"}
              }
            }
          }
        },
        {
          "type": "function",
          "function": {
            "name": "get_conditions",
            "description": "Get the current weather conditions for a city",
            "parameters": {
              "type": "object",
              "required": ["city"],
              "properties": {
                "city": {"type": "string", "description": "The name of the city"}
              }
            }
          }
        }
      ]
    }'
    ```

    **Generate a response with multiple tool results**

    ```shell  theme={"system"}
    curl -s http://localhost:11434/api/chat -H "Content-Type: application/json" -d '{
      "model": "qwen3",
      "messages": [
        {"role": "user", "content": "What are the current weather conditions and temperature in New York and London?"},
        {
          "role": "assistant",
          "tool_calls": [
            {
              "type": "function",
              "function": {
                "index": 0,
                "name": "get_temperature",
                "arguments": {"city": "New York"}
              }
            },
            {
              "type": "function",
              "function": {
                "index": 1,
                "name": "get_conditions",
                "arguments": {"city": "New York"}
              }
            },
            {
              "type": "function",
              "function": {
                "index": 2,
                "name": "get_temperature",
                "arguments": {"city": "London"}
              }
            },
            {
              "type": "function",
              "function": {
                "index": 3,
                "name": "get_conditions",
                "arguments": {"city": "London"}
              }
            }
          ]
        },
        {"role": "tool", "tool_name": "get_temperature", "content": "22°C"},
        {"role": "tool", "tool_name": "get_conditions", "content": "Partly cloudy"},
        {"role": "tool", "tool_name": "get_temperature", "content": "15°C"},
        {"role": "tool", "tool_name": "get_conditions", "content": "Rainy"}
      ],
      "stream": false
    }'
    ```
  </Tab>

  <Tab title="Python">
    ```python  theme={"system"}
    from ollama import chat

    def get_temperature(city: str) -> str:
      """Get the current temperature for a city
      
      Args:
        city: The name of the city

      Returns:
        The current temperature for the city
      """
      temperatures = {
        "New York": "22°C",
        "London": "15°C",
        "Tokyo": "18°C"
      }
      return temperatures.get(city, "Unknown")

    def get_conditions(city: str) -> str:
      """Get the current weather conditions for a city
      
      Args:
        city: The name of the city

      Returns:
        The current weather conditions for the city
      """
      conditions = {
        "New York": "Partly cloudy",
        "London": "Rainy",
        "Tokyo": "Sunny"
      }
      return conditions.get(city, "Unknown")


    messages = [{'role': 'user', 'content': 'What are the current weather conditions and temperature in New York and London?'}]

    # The python client automatically parses functions as a tool schema so we can pass them directly
    # Schemas can be passed directly in the tools list as well 
    response = chat(model='qwen3', messages=messages, tools=[get_temperature, get_conditions], think=True)

    # add the assistant message to the messages
    messages.append(response.message)
    if response.message.tool_calls:
      # process each tool call 
      for call in response.message.tool_calls:
        # execute the appropriate tool
        if call.function.name == 'get_temperature':
          result = get_temperature(**call.function.arguments)
        elif call.function.name == 'get_conditions':
          result = get_conditions(**call.function.arguments)
        else:
          result = 'Unknown tool'
        # add the tool result to the messages
        messages.append({'role': 'tool',  'tool_name': call.function.name, 'content': str(result)})

      # generate the final response
      final_response = chat(model='qwen3', messages=messages, tools=[get_temperature, get_conditions], think=True)
      print(final_response.message.content)
    ```
  </Tab>

  <Tab title="JavaScript">
    ```typescript  theme={"system"}
    import ollama from 'ollama'

    function getTemperature(city: string): string {
      const temperatures: { [key: string]: string } = {
        "New York": "22°C",
        "London": "15°C",
        "Tokyo": "18°C"
      }
      return temperatures[city] || "Unknown"
    }

    function getConditions(city: string): string {
      const conditions: { [key: string]: string } = {
        "New York": "Partly cloudy",
        "London": "Rainy",
        "Tokyo": "Sunny"
      }
      return conditions[city] || "Unknown"
    }

    const tools = [
      {
        type: 'function',
        function: {
          name: 'get_temperature',
          description: 'Get the current temperature for a city',
          parameters: {
            type: 'object',
            required: ['city'],
            properties: {
              city: { type: 'string', description: 'The name of the city' },
            },
          },
        },
      },
      {
        type: 'function',
        function: {
          name: 'get_conditions',
          description: 'Get the current weather conditions for a city',
          parameters: {
            type: 'object',
            required: ['city'],
            properties: {
              city: { type: 'string', description: 'The name of the city' },
            },
          },
        },
      }
    ]

    const messages = [{ role: 'user', content: 'What are the current weather conditions and temperature in New York and London?' }]

    const response = await ollama.chat({
      model: 'qwen3',
      messages,
      tools,
      think: true
    })

    // add the assistant message to the messages
    messages.push(response.message)
    if (response.message.tool_calls) {
      // process each tool call 
      for (const call of response.message.tool_calls) {
        // execute the appropriate tool
        let result: string
        if (call.function.name === 'get_temperature') {
          const args = call.function.arguments as { city: string }
          result = getTemperature(args.city)
        } else if (call.function.name === 'get_conditions') {
          const args = call.function.arguments as { city: string }
          result = getConditions(args.city)
        } else {
          result = 'Unknown tool'
        }
        // add the tool result to the messages
        messages.push({ role: 'tool', tool_name: call.function.name, content: result })
      }

      // generate the final response
      const finalResponse = await ollama.chat({ model: 'qwen3', messages, tools, think: true })
      console.log(finalResponse.message.content)
    }
    ```
  </Tab>
</Tabs>

## Multi-turn tool calling (Agent loop)

An agent loop allows the model to decide when to invoke tools and incorporate their results into its replies.

It also might help to tell the model that it is in a loop and can make multiple tool calls.

<Tabs>
  <Tab title="Python">
    ```python  theme={"system"}
    from ollama import chat, ChatResponse


    def add(a: int, b: int) -> int:
      """Add two numbers"""
      """
      Args:
        a: The first number
        b: The second number

      Returns:
        The sum of the two numbers
      """
      return a + b


    def multiply(a: int, b: int) -> int:
      """Multiply two numbers"""
      """
      Args:
        a: The first number
        b: The second number

      Returns:
        The product of the two numbers
      """
      return a * b


    available_functions = {
      'add': add,
      'multiply': multiply,
    }

    messages = [{'role': 'user', 'content': 'What is (11434+12341)*412?'}]
    while True:
        response: ChatResponse = chat(
            model='qwen3',
            messages=messages,
            tools=[add, multiply],
            think=True,
        )
        messages.append(response.message)
        print("Thinking: ", response.message.thinking)
        print("Content: ", response.message.content)
        if response.message.tool_calls:
            for tc in response.message.tool_calls:
                if tc.function.name in available_functions:
                    print(f"Calling {tc.function.name} with arguments {tc.function.arguments}")
                    result = available_functions[tc.function.name](**tc.function.arguments)
                    print(f"Result: {result}")
                    # add the tool result to the messages
                    messages.append({'role': 'tool', 'tool_name': tc.function.name, 'content': str(result)})
        else:
            # end the loop when there are no more tool calls
            break
      # continue the loop with the updated messages
    ```
  </Tab>

  <Tab title="JavaScript">
    ```typescript  theme={"system"}
    import ollama from 'ollama'

    type ToolName = 'add' | 'multiply'

    function add(a: number, b: number): number {
      return a + b
    }

    function multiply(a: number, b: number): number {
      return a * b
    }

    const availableFunctions: Record<ToolName, (a: number, b: number) => number> = {
      add,
      multiply,
    }

    const tools = [
      {
        type: 'function',
        function: {
          name: 'add',
          description: 'Add two numbers',
          parameters: {
            type: 'object',
            required: ['a', 'b'],
            properties: {
              a: { type: 'integer', description: 'The first number' },
              b: { type: 'integer', description: 'The second number' },
            },
          },
        },
      },
      {
        type: 'function',
        function: {
          name: 'multiply',
          description: 'Multiply two numbers',
          parameters: {
            type: 'object',
            required: ['a', 'b'],
            properties: {
              a: { type: 'integer', description: 'The first number' },
              b: { type: 'integer', description: 'The second number' },
            },
          },
        },
      },
    ]

    async function agentLoop() {
      const messages = [{ role: 'user', content: 'What is (11434+12341)*412?' }]

      while (true) {
        const response = await ollama.chat({
          model: 'qwen3',
          messages,
          tools,
          think: true,
        })

        messages.push(response.message)
        console.log('Thinking:', response.message.thinking)
        console.log('Content:', response.message.content)

        const toolCalls = response.message.tool_calls ?? []
        if (toolCalls.length) {
          for (const call of toolCalls) {
            const fn = availableFunctions[call.function.name as ToolName]
            if (!fn) {
              continue
            }

            const args = call.function.arguments as { a: number; b: number }
            console.log(`Calling ${call.function.name} with arguments`, args)
            const result = fn(args.a, args.b)
            console.log(`Result: ${result}`)
            messages.push({ role: 'tool', tool_name: call.function.name, content: String(result) })
          }
        } else {
          break
        }
      }
    }

    agentLoop().catch(console.error)
    ```
  </Tab>
</Tabs>

## Tool calling with streaming

When streaming, gather every chunk of `thinking`, `content`, and `tool_calls`, then return those fields together with any tool results in the follow-up request.

<Tabs>
  <Tab title="Python">
    ```python  theme={"system"}
    from ollama import chat 


    def get_temperature(city: str) -> str:
      """Get the current temperature for a city
      
      Args:
        city: The name of the city

      Returns:
        The current temperature for the city
      """
      temperatures = {
        'New York': '22°C',
        'London': '15°C',
      }
      return temperatures.get(city, 'Unknown')


    messages = [{'role': 'user', 'content': "What is the temperature in New York?"}]

    while True:
      stream = chat(
        model='qwen3',
        messages=messages,
        tools=[get_temperature],
        stream=True,
        think=True,
      )

      thinking = ''
      content = ''
      tool_calls = []

      done_thinking = False
      # accumulate the partial fields
      for chunk in stream:
        if chunk.message.thinking:
          thinking += chunk.message.thinking
          print(chunk.message.thinking, end='', flush=True)
        if chunk.message.content:
          if not done_thinking:
            done_thinking = True
            print('\n')
          content += chunk.message.content
          print(chunk.message.content, end='', flush=True)
        if chunk.message.tool_calls:
          tool_calls.extend(chunk.message.tool_calls)
          print(chunk.message.tool_calls)

      # append accumulated fields to the messages
      if thinking or content or tool_calls:
        messages.append({'role': 'assistant', 'thinking': thinking, 'content': content, 'tool_calls': tool_calls})

      if not tool_calls:
        break

      for call in tool_calls:
        if call.function.name == 'get_temperature':
          result = get_temperature(**call.function.arguments)
        else:
          result = 'Unknown tool'
        messages.append({'role': 'tool', 'tool_name': call.function.name, 'content': result})
    ```
  </Tab>

  <Tab title="JavaScript">
    ```typescript  theme={"system"}
    import ollama from 'ollama'

    function getTemperature(city: string): string {
      const temperatures: Record<string, string> = {
        'New York': '22°C',
        'London': '15°C',
      }
      return temperatures[city] ?? 'Unknown'
    }

    const getTemperatureTool = {
      type: 'function',
      function: {
        name: 'get_temperature',
        description: 'Get the current temperature for a city',
        parameters: {
          type: 'object',
          required: ['city'],
          properties: {
            city: { type: 'string', description: 'The name of the city' },
          },
        },
      },
    }

    async function agentLoop() {
      const messages = [{ role: 'user', content: "What is the temperature in New York?" }]

      while (true) {
        const stream = await ollama.chat({
          model: 'qwen3',
          messages,
          tools: [getTemperatureTool],
          stream: true,
          think: true,
        })

        let thinking = ''
        let content = ''
        const toolCalls: any[] = []
        let doneThinking = false

        for await (const chunk of stream) {
          if (chunk.message.thinking) {
            thinking += chunk.message.thinking
            process.stdout.write(chunk.message.thinking)
          }
          if (chunk.message.content) {
            if (!doneThinking) {
              doneThinking = true
              process.stdout.write('\n')
            }
            content += chunk.message.content
            process.stdout.write(chunk.message.content)
          }
          if (chunk.message.tool_calls?.length) {
            toolCalls.push(...chunk.message.tool_calls)
            console.log(chunk.message.tool_calls)
          }
        }

        if (thinking || content || toolCalls.length) {
          messages.push({ role: 'assistant', thinking, content, tool_calls: toolCalls } as any)
        }

        if (!toolCalls.length) {
          break
        }

        for (const call of toolCalls) {
          if (call.function.name === 'get_temperature') {
            const args = call.function.arguments as { city: string }
            const result = getTemperature(args.city)
            messages.push({ role: 'tool', tool_name: call.function.name, content: result } )
          } else {
            messages.push({ role: 'tool', tool_name: call.function.name, content: 'Unknown tool' } )
          }
        }
      }
    }

    agentLoop().catch(console.error)
    ```
  </Tab>
</Tabs>

This loop streams the assistant response, accumulates partial fields, passes them back together, and appends the tool results so the model can complete its answer.

## Using functions as tools with Ollama Python SDK

The Python SDK automatically parses functions as a tool schema so we can pass them directly.
Schemas can still be passed if needed.

```python  theme={"system"}
from ollama import chat

def get_temperature(city: str) -> str:
  """Get the current temperature for a city
  
  Args:
    city: The name of the city

  Returns:
    The current temperature for the city
  """
  temperatures = {
    'New York': '22°C',
    'London': '15°C',
  }
  return temperatures.get(city, 'Unknown')

available_functions = {
  'get_temperature': get_temperature,
}
# directly pass the function as part of the tools list
response = chat(model='qwen3', messages=messages, tools=available_functions.values(), think=True)
```

> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Tool calling

Ollama supports tool calling (also known as function calling) which allows a model to invoke tools and incorporate their results into its replies.

## Calling a single tool

Invoke a single tool and include its response in a follow-up request.

Also known as "single-shot" tool calling.

<Tabs>
  <Tab title="cURL">
    ```shell  theme={"system"}
    curl -s http://localhost:11434/api/chat -H "Content-Type: application/json" -d '{
      "model": "qwen3",
      "messages": [{"role": "user", "content": "What is the temperature in New York?"}],
      "stream": false,
      "tools": [
        {
          "type": "function",
          "function": {
            "name": "get_temperature",
            "description": "Get the current temperature for a city",
            "parameters": {
              "type": "object",
              "required": ["city"],
              "properties": {
                "city": {"type": "string", "description": "The name of the city"}
              }
            }
          }
        }
      ]
    }'
    ```

    **Generate a response with a single tool result**

    ```shell  theme={"system"}
    curl -s http://localhost:11434/api/chat -H "Content-Type: application/json" -d '{
      "model": "qwen3",
      "messages": [
        {"role": "user", "content": "What is the temperature in New York?"},
        {
          "role": "assistant",
          "tool_calls": [
            {
              "type": "function",
              "function": {
                "index": 0,
                "name": "get_temperature",
                "arguments": {"city": "New York"}
              }
            }
          ]
        },
        {"role": "tool", "tool_name": "get_temperature", "content": "22°C"}
      ],
      "stream": false
    }'
    ```
  </Tab>

  <Tab title="Python">
    Install the Ollama Python SDK:

    ```bash  theme={"system"}
    # with pip
    pip install ollama -U

    # with uv
    uv add ollama    
    ```

    ```python  theme={"system"}
    from ollama import chat

    def get_temperature(city: str) -> str:
      """Get the current temperature for a city
      
      Args:
        city: The name of the city

      Returns:
        The current temperature for the city
      """
      temperatures = {
        "New York": "22°C",
        "London": "15°C",
        "Tokyo": "18°C",
      }
      return temperatures.get(city, "Unknown")

    messages = [{"role": "user", "content": "What is the temperature in New York?"}]

    # pass functions directly as tools in the tools list or as a JSON schema
    response = chat(model="qwen3", messages=messages, tools=[get_temperature], think=True)

    messages.append(response.message)
    if response.message.tool_calls:
      # only recommended for models which only return a single tool call
      call = response.message.tool_calls[0]
      result = get_temperature(**call.function.arguments)
      # add the tool result to the messages
      messages.append({"role": "tool", "tool_name": call.function.name, "content": str(result)})

      final_response = chat(model="qwen3", messages=messages, tools=[get_temperature], think=True)
      print(final_response.message.content)
    ```
  </Tab>

  <Tab title="JavaScript">
    Install the Ollama JavaScript library:

    ```bash  theme={"system"}
    # with npm
    npm i ollama

    # with bun
    bun i ollama
    ```

    ```typescript  theme={"system"}
    import ollama from 'ollama'

    function getTemperature(city: string): string {
      const temperatures: Record<string, string> = {
        'New York': '22°C',
        'London': '15°C',
        'Tokyo': '18°C',
      }
      return temperatures[city] ?? 'Unknown'
    }

    const tools = [
      {
        type: 'function',
        function: {
          name: 'get_temperature',
          description: 'Get the current temperature for a city',
          parameters: {
            type: 'object',
            required: ['city'],
            properties: {
              city: { type: 'string', description: 'The name of the city' },
            },
          },
        },
      },
    ]

    const messages = [{ role: 'user', content: "What is the temperature in New York?" }]

    const response = await ollama.chat({
      model: 'qwen3',
      messages,
      tools,
      think: true,
    })

    messages.push(response.message)
    if (response.message.tool_calls?.length) {
      // only recommended for models which only return a single tool call
      const call = response.message.tool_calls[0]
      const args = call.function.arguments as { city: string }
      const result = getTemperature(args.city)
      // add the tool result to the messages
      messages.push({ role: 'tool', tool_name: call.function.name, content: result })

      // generate the final response
      const finalResponse = await ollama.chat({ model: 'qwen3', messages, tools, think: true })
      console.log(finalResponse.message.content)
    }
    ```
  </Tab>
</Tabs>

## Parallel tool calling

<Tabs>
  <Tab title="cURL">
    Request multiple tool calls in parallel, then send all tool responses back to the model.

    ```shell  theme={"system"}
    curl -s http://localhost:11434/api/chat -H "Content-Type: application/json" -d '{
      "model": "qwen3",
      "messages": [{"role": "user", "content": "What are the current weather conditions and temperature in New York and London?"}],
      "stream": false,
      "tools": [
        {
          "type": "function",
          "function": {
            "name": "get_temperature",
            "description": "Get the current temperature for a city",
            "parameters": {
              "type": "object",
              "required": ["city"],
              "properties": {
                "city": {"type": "string", "description": "The name of the city"}
              }
            }
          }
        },
        {
          "type": "function",
          "function": {
            "name": "get_conditions",
            "description": "Get the current weather conditions for a city",
            "parameters": {
              "type": "object",
              "required": ["city"],
              "properties": {
                "city": {"type": "string", "description": "The name of the city"}
              }
            }
          }
        }
      ]
    }'
    ```

    **Generate a response with multiple tool results**

    ```shell  theme={"system"}
    curl -s http://localhost:11434/api/chat -H "Content-Type: application/json" -d '{
      "model": "qwen3",
      "messages": [
        {"role": "user", "content": "What are the current weather conditions and temperature in New York and London?"},
        {
          "role": "assistant",
          "tool_calls": [
            {
              "type": "function",
              "function": {
                "index": 0,
                "name": "get_temperature",
                "arguments": {"city": "New York"}
              }
            },
            {
              "type": "function",
              "function": {
                "index": 1,
                "name": "get_conditions",
                "arguments": {"city": "New York"}
              }
            },
            {
              "type": "function",
              "function": {
                "index": 2,
                "name": "get_temperature",
                "arguments": {"city": "London"}
              }
            },
            {
              "type": "function",
              "function": {
                "index": 3,
                "name": "get_conditions",
                "arguments": {"city": "London"}
              }
            }
          ]
        },
        {"role": "tool", "tool_name": "get_temperature", "content": "22°C"},
        {"role": "tool", "tool_name": "get_conditions", "content": "Partly cloudy"},
        {"role": "tool", "tool_name": "get_temperature", "content": "15°C"},
        {"role": "tool", "tool_name": "get_conditions", "content": "Rainy"}
      ],
      "stream": false
    }'
    ```
  </Tab>

  <Tab title="Python">
    ```python  theme={"system"}
    from ollama import chat

    def get_temperature(city: str) -> str:
      """Get the current temperature for a city
      
      Args:
        city: The name of the city

      Returns:
        The current temperature for the city
      """
      temperatures = {
        "New York": "22°C",
        "London": "15°C",
        "Tokyo": "18°C"
      }
      return temperatures.get(city, "Unknown")

    def get_conditions(city: str) -> str:
      """Get the current weather conditions for a city
      
      Args:
        city: The name of the city

      Returns:
        The current weather conditions for the city
      """
      conditions = {
        "New York": "Partly cloudy",
        "London": "Rainy",
        "Tokyo": "Sunny"
      }
      return conditions.get(city, "Unknown")


    messages = [{'role': 'user', 'content': 'What are the current weather conditions and temperature in New York and London?'}]

    # The python client automatically parses functions as a tool schema so we can pass them directly
    # Schemas can be passed directly in the tools list as well 
    response = chat(model='qwen3', messages=messages, tools=[get_temperature, get_conditions], think=True)

    # add the assistant message to the messages
    messages.append(response.message)
    if response.message.tool_calls:
      # process each tool call 
      for call in response.message.tool_calls:
        # execute the appropriate tool
        if call.function.name == 'get_temperature':
          result = get_temperature(**call.function.arguments)
        elif call.function.name == 'get_conditions':
          result = get_conditions(**call.function.arguments)
        else:
          result = 'Unknown tool'
        # add the tool result to the messages
        messages.append({'role': 'tool',  'tool_name': call.function.name, 'content': str(result)})

      # generate the final response
      final_response = chat(model='qwen3', messages=messages, tools=[get_temperature, get_conditions], think=True)
      print(final_response.message.content)
    ```
  </Tab>

  <Tab title="JavaScript">
    ```typescript  theme={"system"}
    import ollama from 'ollama'

    function getTemperature(city: string): string {
      const temperatures: { [key: string]: string } = {
        "New York": "22°C",
        "London": "15°C",
        "Tokyo": "18°C"
      }
      return temperatures[city] || "Unknown"
    }

    function getConditions(city: string): string {
      const conditions: { [key: string]: string } = {
        "New York": "Partly cloudy",
        "London": "Rainy",
        "Tokyo": "Sunny"
      }
      return conditions[city] || "Unknown"
    }

    const tools = [
      {
        type: 'function',
        function: {
          name: 'get_temperature',
          description: 'Get the current temperature for a city',
          parameters: {
            type: 'object',
            required: ['city'],
            properties: {
              city: { type: 'string', description: 'The name of the city' },
            },
          },
        },
      },
      {
        type: 'function',
        function: {
          name: 'get_conditions',
          description: 'Get the current weather conditions for a city',
          parameters: {
            type: 'object',
            required: ['city'],
            properties: {
              city: { type: 'string', description: 'The name of the city' },
            },
          },
        },
      }
    ]

    const messages = [{ role: 'user', content: 'What are the current weather conditions and temperature in New York and London?' }]

    const response = await ollama.chat({
      model: 'qwen3',
      messages,
      tools,
      think: true
    })

    // add the assistant message to the messages
    messages.push(response.message)
    if (response.message.tool_calls) {
      // process each tool call 
      for (const call of response.message.tool_calls) {
        // execute the appropriate tool
        let result: string
        if (call.function.name === 'get_temperature') {
          const args = call.function.arguments as { city: string }
          result = getTemperature(args.city)
        } else if (call.function.name === 'get_conditions') {
          const args = call.function.arguments as { city: string }
          result = getConditions(args.city)
        } else {
          result = 'Unknown tool'
        }
        // add the tool result to the messages
        messages.push({ role: 'tool', tool_name: call.function.name, content: result })
      }

      // generate the final response
      const finalResponse = await ollama.chat({ model: 'qwen3', messages, tools, think: true })
      console.log(finalResponse.message.content)
    }
    ```
  </Tab>
</Tabs>

## Multi-turn tool calling (Agent loop)

An agent loop allows the model to decide when to invoke tools and incorporate their results into its replies.

It also might help to tell the model that it is in a loop and can make multiple tool calls.

<Tabs>
  <Tab title="Python">
    ```python  theme={"system"}
    from ollama import chat, ChatResponse


    def add(a: int, b: int) -> int:
      """Add two numbers"""
      """
      Args:
        a: The first number
        b: The second number

      Returns:
        The sum of the two numbers
      """
      return a + b


    def multiply(a: int, b: int) -> int:
      """Multiply two numbers"""
      """
      Args:
        a: The first number
        b: The second number

      Returns:
        The product of the two numbers
      """
      return a * b


    available_functions = {
      'add': add,
      'multiply': multiply,
    }

    messages = [{'role': 'user', 'content': 'What is (11434+12341)*412?'}]
    while True:
        response: ChatResponse = chat(
            model='qwen3',
            messages=messages,
            tools=[add, multiply],
            think=True,
        )
        messages.append(response.message)
        print("Thinking: ", response.message.thinking)
        print("Content: ", response.message.content)
        if response.message.tool_calls:
            for tc in response.message.tool_calls:
                if tc.function.name in available_functions:
                    print(f"Calling {tc.function.name} with arguments {tc.function.arguments}")
                    result = available_functions[tc.function.name](**tc.function.arguments)
                    print(f"Result: {result}")
                    # add the tool result to the messages
                    messages.append({'role': 'tool', 'tool_name': tc.function.name, 'content': str(result)})
        else:
            # end the loop when there are no more tool calls
            break
      # continue the loop with the updated messages
    ```
  </Tab>

  <Tab title="JavaScript">
    ```typescript  theme={"system"}
    import ollama from 'ollama'

    type ToolName = 'add' | 'multiply'

    function add(a: number, b: number): number {
      return a + b
    }

    function multiply(a: number, b: number): number {
      return a * b
    }

    const availableFunctions: Record<ToolName, (a: number, b: number) => number> = {
      add,
      multiply,
    }

    const tools = [
      {
        type: 'function',
        function: {
          name: 'add',
          description: 'Add two numbers',
          parameters: {
            type: 'object',
            required: ['a', 'b'],
            properties: {
              a: { type: 'integer', description: 'The first number' },
              b: { type: 'integer', description: 'The second number' },
            },
          },
        },
      },
      {
        type: 'function',
        function: {
          name: 'multiply',
          description: 'Multiply two numbers',
          parameters: {
            type: 'object',
            required: ['a', 'b'],
            properties: {
              a: { type: 'integer', description: 'The first number' },
              b: { type: 'integer', description: 'The second number' },
            },
          },
        },
      },
    ]

    async function agentLoop() {
      const messages = [{ role: 'user', content: 'What is (11434+12341)*412?' }]

      while (true) {
        const response = await ollama.chat({
          model: 'qwen3',
          messages,
          tools,
          think: true,
        })

        messages.push(response.message)
        console.log('Thinking:', response.message.thinking)
        console.log('Content:', response.message.content)

        const toolCalls = response.message.tool_calls ?? []
        if (toolCalls.length) {
          for (const call of toolCalls) {
            const fn = availableFunctions[call.function.name as ToolName]
            if (!fn) {
              continue
            }

            const args = call.function.arguments as { a: number; b: number }
            console.log(`Calling ${call.function.name} with arguments`, args)
            const result = fn(args.a, args.b)
            console.log(`Result: ${result}`)
            messages.push({ role: 'tool', tool_name: call.function.name, content: String(result) })
          }
        } else {
          break
        }
      }
    }

    agentLoop().catch(console.error)
    ```
  </Tab>
</Tabs>

## Tool calling with streaming

When streaming, gather every chunk of `thinking`, `content`, and `tool_calls`, then return those fields together with any tool results in the follow-up request.

<Tabs>
  <Tab title="Python">
    ```python  theme={"system"}
    from ollama import chat 


    def get_temperature(city: str) -> str:
      """Get the current temperature for a city
      
      Args:
        city: The name of the city

      Returns:
        The current temperature for the city
      """
      temperatures = {
        'New York': '22°C',
        'London': '15°C',
      }
      return temperatures.get(city, 'Unknown')


    messages = [{'role': 'user', 'content': "What is the temperature in New York?"}]

    while True:
      stream = chat(
        model='qwen3',
        messages=messages,
        tools=[get_temperature],
        stream=True,
        think=True,
      )

      thinking = ''
      content = ''
      tool_calls = []

      done_thinking = False
      # accumulate the partial fields
      for chunk in stream:
        if chunk.message.thinking:
          thinking += chunk.message.thinking
          print(chunk.message.thinking, end='', flush=True)
        if chunk.message.content:
          if not done_thinking:
            done_thinking = True
            print('\n')
          content += chunk.message.content
          print(chunk.message.content, end='', flush=True)
        if chunk.message.tool_calls:
          tool_calls.extend(chunk.message.tool_calls)
          print(chunk.message.tool_calls)

      # append accumulated fields to the messages
      if thinking or content or tool_calls:
        messages.append({'role': 'assistant', 'thinking': thinking, 'content': content, 'tool_calls': tool_calls})

      if not tool_calls:
        break

      for call in tool_calls:
        if call.function.name == 'get_temperature':
          result = get_temperature(**call.function.arguments)
        else:
          result = 'Unknown tool'
        messages.append({'role': 'tool', 'tool_name': call.function.name, 'content': result})
    ```
  </Tab>

  <Tab title="JavaScript">
    ```typescript  theme={"system"}
    import ollama from 'ollama'

    function getTemperature(city: string): string {
      const temperatures: Record<string, string> = {
        'New York': '22°C',
        'London': '15°C',
      }
      return temperatures[city] ?? 'Unknown'
    }

    const getTemperatureTool = {
      type: 'function',
      function: {
        name: 'get_temperature',
        description: 'Get the current temperature for a city',
        parameters: {
          type: 'object',
          required: ['city'],
          properties: {
            city: { type: 'string', description: 'The name of the city' },
          },
        },
      },
    }

    async function agentLoop() {
      const messages = [{ role: 'user', content: "What is the temperature in New York?" }]

      while (true) {
        const stream = await ollama.chat({
          model: 'qwen3',
          messages,
          tools: [getTemperatureTool],
          stream: true,
          think: true,
        })

        let thinking = ''
        let content = ''
        const toolCalls: any[] = []
        let doneThinking = false

        for await (const chunk of stream) {
          if (chunk.message.thinking) {
            thinking += chunk.message.thinking
            process.stdout.write(chunk.message.thinking)
          }
          if (chunk.message.content) {
            if (!doneThinking) {
              doneThinking = true
              process.stdout.write('\n')
            }
            content += chunk.message.content
            process.stdout.write(chunk.message.content)
          }
          if (chunk.message.tool_calls?.length) {
            toolCalls.push(...chunk.message.tool_calls)
            console.log(chunk.message.tool_calls)
          }
        }

        if (thinking || content || toolCalls.length) {
          messages.push({ role: 'assistant', thinking, content, tool_calls: toolCalls } as any)
        }

        if (!toolCalls.length) {
          break
        }

        for (const call of toolCalls) {
          if (call.function.name === 'get_temperature') {
            const args = call.function.arguments as { city: string }
            const result = getTemperature(args.city)
            messages.push({ role: 'tool', tool_name: call.function.name, content: result } )
          } else {
            messages.push({ role: 'tool', tool_name: call.function.name, content: 'Unknown tool' } )
          }
        }
      }
    }

    agentLoop().catch(console.error)
    ```
  </Tab>
</Tabs>

This loop streams the assistant response, accumulates partial fields, passes them back together, and appends the tool results so the model can complete its answer.

## Using functions as tools with Ollama Python SDK

The Python SDK automatically parses functions as a tool schema so we can pass them directly.
Schemas can still be passed if needed.

```python  theme={"system"}
from ollama import chat

def get_temperature(city: str) -> str:
  """Get the current temperature for a city
  
  Args:
    city: The name of the city

  Returns:
    The current temperature for the city
  """
  temperatures = {
    'New York': '22°C',
    'London': '15°C',
  }
  return temperatures.get(city, 'Unknown')

available_functions = {
  'get_temperature': get_temperature,
}
# directly pass the function as part of the tools list
response = chat(model='qwen3', messages=messages, tools=available_functions.values(), think=True)
```

> ## Documentation Index
> Fetch the complete documentation index at: https://docs.ollama.com/llms.txt
> Use this file to discover all available pages before exploring further.

# Web search

Ollama's web search API can be used to augment models with the latest information to reduce hallucinations and improve accuracy.

Web search is provided as a REST API with deeper tool integrations in the Python and JavaScript libraries. This also enables models like OpenAI’s gpt-oss models to conduct long-running research tasks.

## Authentication

For access to Ollama's web search API, create an [API key](https://ollama.com/settings/keys). A free Ollama account is required.

## Web search API

Performs a web search for a single query and returns relevant results.

### Request

`POST https://ollama.com/api/web_search`

* `query` (string, required): the search query string
* `max_results` (integer, optional): maximum results to return (default 5, max 10)

### Response

Returns an object containing:

* `results` (array): array of search result objects, each containing:
    * `title` (string): the title of the web page
    * `url` (string): the URL of the web page
    * `content` (string): relevant content snippet from the web page

### Examples

<Note>
  Ensure OLLAMA\_API\_KEY is set or it must be passed in the Authorization header.
</Note>

#### cURL Request

```bash  theme={"system"}
curl https://ollama.com/api/web_search \
  --header "Authorization: Bearer $OLLAMA_API_KEY" \
	-d '{
	  "query":"what is ollama?"
	}'
```

**Response**

```json  theme={"system"}
{
  "results": [
    {
      "title": "Ollama",
      "url": "https://ollama.com/",
      "content": "Cloud models are now available..."
    },
    {
      "title": "What is Ollama? Introduction to the AI model management tool",
      "url": "https://www.hostinger.com/tutorials/what-is-ollama",
      "content": "Ariffud M. 6min Read..."
    },
    {
      "title": "Ollama Explained: Transforming AI Accessibility and Language ...",
      "url": "https://www.geeksforgeeks.org/artificial-intelligence/ollama-explained-transforming-ai-accessibility-and-language-processing/",
      "content": "Data Science Data Science Projects Data Analysis..."
    }
  ]
}
```

#### Python library

```python  theme={"system"}
import ollama
response = ollama.web_search("What is Ollama?")
print(response)
```

**Example output**

```python  theme={"system"}

results = [
    {
        "title": "Ollama",
        "url": "https://ollama.com/",
        "content": "Cloud models are now available in Ollama..."
    },
    {
        "title": "What is Ollama? Features, Pricing, and Use Cases - Walturn",
        "url": "https://www.walturn.com/insights/what-is-ollama-features-pricing-and-use-cases",
        "content": "Our services..."
    },
    {
        "title": "Complete Ollama Guide: Installation, Usage & Code Examples",
        "url": "https://collabnix.com/complete-ollama-guide-installation-usage-code-examples",
        "content": "Join our Discord Server..."
    }
]

```

More Ollama [Python example](https://github.com/ollama/ollama-python/blob/main/examples/web-search.py)

#### JavaScript Library

```tsx  theme={"system"}
import { Ollama } from "ollama";

const client = new Ollama();
const results = await client.webSearch("what is ollama?");
console.log(JSON.stringify(results, null, 2));
```

**Example output**

```json  theme={"system"}
{
  "results": [
    {
      "title": "Ollama",
      "url": "https://ollama.com/",
      "content": "Cloud models are now available..."
    },
    {
      "title": "What is Ollama? Introduction to the AI model management tool",
      "url": "https://www.hostinger.com/tutorials/what-is-ollama",
      "content": "Ollama is an open-source tool..."
    },
    {
      "title": "Ollama Explained: Transforming AI Accessibility and Language Processing",
      "url": "https://www.geeksforgeeks.org/artificial-intelligence/ollama-explained-transforming-ai-accessibility-and-language-processing/",
      "content": "Ollama is a groundbreaking..."
    }
  ]
}
```

More Ollama [JavaScript example](https://github.com/ollama/ollama-js/blob/main/examples/websearch/websearch-tools.ts)

## Web fetch API

Fetches a single web page by URL and returns its content.

### Request

`POST https://ollama.com/api/web_fetch`

* `url` (string, required): the URL to fetch

### Response

Returns an object containing:

* `title` (string): the title of the web page
* `content` (string): the main content of the web page
* `links` (array): array of links found on the page

### Examples

#### cURL Request

```python  theme={"system"}
curl --request POST \
  --url https://ollama.com/api/web_fetch \
  --header "Authorization: Bearer $OLLAMA_API_KEY" \
  --header 'Content-Type: application/json' \
  --data '{
      "url": "ollama.com"
  }'
```

**Response**

```json  theme={"system"}
{
  "title": "Ollama",
  "content": "[Cloud models](https://ollama.com/blog/cloud-models) are now available in Ollama...",
  "links": [
    "http://ollama.com/",
    "http://ollama.com/models",
    "https://github.com/ollama/ollama"
  ]

```

#### Python SDK

```python  theme={"system"}
from ollama import web_fetch

result = web_fetch('https://ollama.com')
print(result)
```

**Result**

```python  theme={"system"}
WebFetchResponse(
    title='Ollama',
    content='[Cloud models](https://ollama.com/blog/cloud-models) are now available in Ollama\n\n**Chat & build
with open models**\n\n[Download](https://ollama.com/download) [Explore
models](https://ollama.com/models)\n\nAvailable for macOS, Windows, and Linux',
    links=['https://ollama.com/', 'https://ollama.com/models', 'https://github.com/ollama/ollama']
)
```

#### JavaScript SDK

```tsx  theme={"system"}
import { Ollama } from "ollama";

const client = new Ollama();
const fetchResult = await client.webFetch("https://ollama.com");
console.log(JSON.stringify(fetchResult, null, 2));
```

**Result**

```json  theme={"system"}
{
  "title": "Ollama",
  "content": "[Cloud models](https://ollama.com/blog/cloud-models) are now available in Ollama...",
  "links": [
    "https://ollama.com/",
    "https://ollama.com/models",
    "https://github.com/ollama/ollama"
  ]
}
```

## Building a search agent

Use Ollama’s web search API as a tool to build a mini search agent.

This example uses Alibaba’s Qwen 3 model with 4B parameters.

```bash  theme={"system"}
ollama pull qwen3:4b
```

```python  theme={"system"}
from ollama import chat, web_fetch, web_search

available_tools = {'web_search': web_search, 'web_fetch': web_fetch}

messages = [{'role': 'user', 'content': "what is ollama's new engine"}]

while True:
  response = chat(
    model='qwen3:4b',
    messages=messages,
    tools=[web_search, web_fetch],
    think=True
    )
  if response.message.thinking:
    print('Thinking: ', response.message.thinking)
  if response.message.content:
    print('Content: ', response.message.content)
  messages.append(response.message)
  if response.message.tool_calls:
    print('Tool calls: ', response.message.tool_calls)
    for tool_call in response.message.tool_calls:
      function_to_call = available_tools.get(tool_call.function.name)
      if function_to_call:
        args = tool_call.function.arguments
        result = function_to_call(**args)
        print('Result: ', str(result)[:200]+'...')
        # Result is truncated for limited context lengths
        messages.append({'role': 'tool', 'content': str(result)[:2000 * 4], 'tool_name': tool_call.function.name})
      else:
        messages.append({'role': 'tool', 'content': f'Tool {tool_call.function.name} not found', 'tool_name': tool_call.function.name})
  else:
    break
```

**Result**

```
Thinking:  Okay, the user is asking about Ollama's new engine. I need to figure out what they're referring to. Ollama is a company that develops large language models, so maybe they've released a new model or an updated version of their existing engine....

Tool calls:  [ToolCall(function=Function(name='web_search', arguments={'max_results': 3, 'query': 'Ollama new engine'}))]
Result:  results=[WebSearchResult(content='# New model scheduling\n\n## September 23, 2025\n\nOllama now includes a significantly improved model scheduling system. Ahead of running a model, Ollama’s new engine

Thinking:  Okay, the user asked about Ollama's new engine. Let me look at the search results.

First result is from September 23, 2025, talking about new model scheduling. It mentions improved memory management, reduced crashes, better GPU utilization, and multi-GPU performance. Examples show speed improvements and accurate memory reporting. Supported models include gemma3, llama4, qwen3, etc...

Content:  Ollama has introduced two key updates to its engine, both released in 2025:

1. **Enhanced Model Scheduling (September 23, 2025)**
   - **Precision Memory Management**: Exact memory allocation reduces out-of-memory crashes and optimizes GPU utilization.
   - **Performance Gains**: Examples show significant speed improvements (e.g., 85.54 tokens/s vs 52.02 tokens/s) and full GPU layer utilization.
   - **Multi-GPU Support**: Improved efficiency across multiple GPUs, with accurate memory reporting via tools like `nvidia-smi`.
   - **Supported Models**: Includes `gemma3`, `llama4`, `qwen3`, `mistral-small3.2`, and more.

2. **Multimodal Engine (May 15, 2025)**
   - **Vision Support**: First-class support for vision models, including `llama4:scout` (109B parameters), `gemma3`, `qwen2.5vl`, and `mistral-small3.1`.
   - **Multimodal Tasks**: Examples include identifying animals in multiple images, answering location-based questions from videos, and document scanning.

These updates highlight Ollama's focus on efficiency, performance, and expanded capabilities for both text and vision tasks.
```

### Context length and agents

Web search results can return thousands of tokens. It is recommended to increase the context length of the model to at least \~32000 tokens. Search agents work best with full context length. [Ollama's cloud models](https://docs.ollama.com/cloud) run at the full context length.

## MCP Server

You can enable web search in any MCP client through the [Python MCP server](https://github.com/ollama/ollama-python/blob/main/examples/web-search-mcp.py).

### Cline

Ollama's web search can be integrated with Cline easily using the MCP server configuration.

`Manage MCP Servers` > `Configure MCP Servers` > Add the following configuration:

```json  theme={"system"}
{
  "mcpServers": {
    "web_search_and_fetch": {
      "type": "stdio",
      "command": "uv",
      "args": ["run", "path/to/web-search-mcp.py"],
      "env": { "OLLAMA_API_KEY": "your_api_key_here" }
    }
  }
}
```

<img src="https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/cline-mcp.png?fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=046239fbe74a8e928752b97b1a8954fa" alt="Cline MCP Configuration" data-og-width="852" width="852" data-og-height="1078" height="1078" data-path="images/cline-mcp.png" data-optimize="true" data-opv="3" srcset="https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/cline-mcp.png?w=280&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=251a7ae4c99cafbeff8867a3cdefc854 280w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/cline-mcp.png?w=560&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=bde250f5b99530b1870b5e7069abf10c 560w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/cline-mcp.png?w=840&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=067e154d817a737cd508f74cffa77294 840w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/cline-mcp.png?w=1100&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=c5db90800a313a6b262fcd37ab5be97f 1100w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/cline-mcp.png?w=1650&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=1c20c4081d1e8f13a3da2348c6df1fd0 1650w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/cline-mcp.png?w=2500&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=2dbaea69c8eefd988ec6c065ce966187 2500w" />

### Codex

Ollama works well with OpenAI's Codex tool.

Add the following configuration to `~/.codex/config.toml`

```python  theme={"system"}
[mcp_servers.web_search]
command = "uv"
args = ["run", "path/to/web-search-mcp.py"]
env = { "OLLAMA_API_KEY" = "your_api_key_here" }
```

<img src="https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/codex-mcp.png?fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=775b41bb85af7836b0a5a609de7d1f6f" alt="Codex MCP Configuration" data-og-width="1150" width="1150" data-og-height="1014" height="1014" data-path="images/codex-mcp.png" data-optimize="true" data-opv="3" srcset="https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/codex-mcp.png?w=280&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=165618dddf9daa7f355f71c454ba3f41 280w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/codex-mcp.png?w=560&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=79585e40dfb53f5fffc4a637a5119118 560w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/codex-mcp.png?w=840&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=ca1d7acc055ebdbc409d9f372d9ca3e5 840w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/codex-mcp.png?w=1100&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=603c85032a6b8dd755950c9d29f8fd21 1100w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/codex-mcp.png?w=1650&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=07665e9ee289fdabb9addde3a06bca7a 1650w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/codex-mcp.png?w=2500&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=f885735a8b1c269439f9ccf10424421e 2500w" />

### Goose

Ollama can integrate with Goose via its MCP feature.

<img src="https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/goose-mcp-1.png?fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=5fea6e0aab7865dc950470f004c549e8" alt="Goose MCP Configuration 1" data-og-width="1152" width="1152" data-og-height="1012" height="1012" data-path="images/goose-mcp-1.png" data-optimize="true" data-opv="3" srcset="https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/goose-mcp-1.png?w=280&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=f7ccec9b53d39d84ed10bdedd0335e33 280w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/goose-mcp-1.png?w=560&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=cb5464f221b561eba98c10702222d4fe 560w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/goose-mcp-1.png?w=840&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=0810ea78c85815474a17d5c1d975771a 840w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/goose-mcp-1.png?w=1100&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=67467cb3aaab1183f1f850a4061a7af0 1100w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/goose-mcp-1.png?w=1650&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=2e8e9d972510ba17d542156b8c7a5142 1650w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/goose-mcp-1.png?w=2500&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=f990a9ba7d6daf66e89699617034e6b9 2500w" />

<img src="https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/goose-mcp-2.png?fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=c69c12389f7dd60ef1c53cd10af82a7d" alt="Goose MCP Configuration 2" data-og-width="1146" width="1146" data-og-height="1006" height="1006" data-path="images/goose-mcp-2.png" data-optimize="true" data-opv="3" srcset="https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/goose-mcp-2.png?w=280&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=498deaa0c52aa33e32f4962e0dea9dc7 280w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/goose-mcp-2.png?w=560&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=bb62f0113619a0f572e0017849a65bb5 560w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/goose-mcp-2.png?w=840&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=7035aae8c4163df72f38d885f11e3f1c 840w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/goose-mcp-2.png?w=1100&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=ca8a2966d7c350c6d75d9252f86f7be8 1100w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/goose-mcp-2.png?w=1650&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=a488d0de5bf91dccd78a5187e712ceb2 1650w, https://mintcdn.com/ollama-9269c548/lS1IbrlCxMxm029K/images/goose-mcp-2.png?w=2500&fit=max&auto=format&n=lS1IbrlCxMxm029K&q=85&s=fa84ce84ab908bacd6853048972bff7c 2500w" />

### Other integrations

Ollama can be integrated into most of the tools available either through direct integration of Ollama's API, Python / JavaScript libraries, OpenAI compatible API, and MCP server integration.

