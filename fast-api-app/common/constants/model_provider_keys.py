PROVIDER_MAP = {
    "ollama": {
        "prompt": "prompt_eval_count",
        "response": "eval_count"
    },
    "openai": {
        "root": "usage",
        "prompt": "prompt_tokens",
        "response": "completion_tokens"
    },
    "anthropic": {
        "root": "usage",
        "prompt": "input_tokens",
        "response": "output_tokens"
    },
    "gemini": {
        "root": "usage_metadata",
        "prompt": "prompt_token_count",
        "response": "candidates_token_count"
    }
}