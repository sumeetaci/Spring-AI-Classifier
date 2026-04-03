from common.constants.model_provider_keys import PROVIDER_MAP
class TokenManager:
    def __init__(self):
        # The central dictionary
        self.stats = {}

    def log_usage(self, provider: str, model_name: str, response_data: dict):
        mapping = PROVIDER_MAP.get(provider.lower())
        if not mapping:
            return

        # Extract tokens based on your PROVIDER_MAP
        root_key = mapping.get("root")
        source = response_data.get(root_key, response_data) if root_key else response_data
        
        p_tokens = source.get(mapping["prompt"], 0)
        r_tokens = source.get(mapping["response"], 0)

        # Update the stats dictionary
        if model_name not in self.stats:
            self.stats[model_name] = {"prompt": 0, "response": 0, "total": 0}
        
        self.stats[model_name]["prompt"] += p_tokens
        self.stats[model_name]["response"] += r_tokens
        self.stats[model_name]["total"] += (p_tokens + r_tokens)