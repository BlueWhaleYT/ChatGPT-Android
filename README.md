# ChatGPT

> **Note**: This may not have the same result of what you exactly asked on ChatGPT Official.

This is an **experimental** android app enabling users to chat with AI via [OpenAI](https://openai.com/).

## Screenshots

These are some screenshots that you can have a look before using ChatGPT-Android app.

<div align="left">
<div>
    <img src="/screenshots/screenshot1.jpeg" width="18%" />
    <img src="/screenshots/screenshot2.jpeg" width="18%" />
    <img src="/screenshots/screenshot3.jpeg" width="18%" />
    <img src="/screenshots/screenshot4.jpeg" width="18%" />
    <img src="/screenshots/screenshot5.jpeg" width="18%" />
</div>
</div>

## Features

- Past conversations: i.e. the context highly recommended using it with `gpt-3.5-turbo` model.
- View: You can view the entire message with the bottom sheet.
- Share: Share the things you've got.
- Save: You can save the messages before clearing the context, it will be saved in `storage/emulated/0/ChatGPT/messages`.

## Supported models

- `text-davinci-003`: not recommended for long context.
- `gpt-3.5-turbo`: used by ChatGPT, recommended for long context and has adjustments on tokens streaming (but the context is not unlimited)

## Get Started

In order to use this app, you need to put your own API Key from [OpenAI](https://openai.com/).