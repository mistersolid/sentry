# Claude generated
"""
Sentry
Sentry is an anti-spam and anti-scam bot. Skynet but for the goose servers.
"""

# IMPORTS
from sklearn.base import BaseEstimator, TransformerMixin
import pandas as pd
import numpy as np
from scipy.sparse import csr_matrix


class EdgeCaseFeatureExtractor(BaseEstimator, TransformerMixin):
    """
    Hand-crafted features targeting specific edge cases in 'asking to ask' detection.
    Each feature disambiguates a known failure mode.
    """

    def fit(self, X, y=None):
        return self

    def transform(self, X):
        if isinstance(X, np.ndarray):
            X = pd.Series(X.flatten())
        elif not isinstance(X, pd.Series):
            X = pd.Series(X)

        low = X.str.lower().str.strip()
        features = {}

        # ---- NEGATIVE SIGNALS (suggests NOT asking-to-ask) ----

        # "where can I ask" — asking WHERE, not asking permission
        features['where_can_i_ask'] = low.str.contains(
            r'where\s+(?:can|should|do|could|may|would)\s+i\s+ask', na=False
        ).astype(float)

        # Third-person subject — "you can ask", "they can ask"
        features['third_person_ask'] = low.str.contains(
            r'\b(?:you|they|he|she|we|people|someone|anyone|everybody|everyone|students|members)'
            r'\s+(?:can|could|should|may|might)\s+ask', na=False
        ).astype(float)

        # Imperative/directive — "just ask", "ask your question"
        features['imperative_ask'] = low.str.contains(
            r'(?:just\s+ask|go\s+ahead\s+and\s+ask|ask\s+your\s+question'
            r'|ask\s+(?:him|her|them|it))', na=False
        ).astype(float)

        # Meta-commentary — quoting/mocking "can I ask a question"
        features['meta_reference'] = low.str.contains(
            r'(?:people\s+(?:just\s+)?say|like\s+imagine|along\s+the\s+lines'
            r'|lmfao.*say|say.*can i ask|"can i ask|classic\s+.*ask'
            r'|stop\s+saying|why\s+do\s+people|so\s+annoying\s+when'
            r'|dont\s+ask\s+to\s+ask|asking\s+to\s+ask|who\s+says\s+can\s+i'
            r'|ask\s+to\s+ask|you\s+dont\s+need\s+to\s+(?:say|ask)'
            r'|pro\s+tip.*just\s+ask|psa.*just\s+ask|reminder.*just\s+ask'
            r'|nobody\s+needs\s+permission|every\s+day\s+someone.*asks?\s+if'
            r'|wastes?\s+every)', na=False
        ).astype(float)

        # "where ... channel/server/place"
        features['location_where'] = low.str.contains(
            r'where\s+.*(?:channel|server|place|post|thread)', na=False
        ).astype(float)

        # Starts with "you can" — giving permission, not seeking it
        features['starts_you_can'] = low.str.contains(
            r'^(?:you\s+can|they\s+can|one\s+can)', na=False
        ).astype(float)

        # "ask him/her/them" — referring to asking someone else
        features['ask_third_party'] = low.str.contains(
            r'ask\s+(?:him|her|them|the\s+\w+|your|our'
            r'|my\s+(?:friend|professor|teacher|roommate|TA|boss|mom|dad|brother|sister))',
            na=False
        ).astype(float)

        # "I can ask [person]" — offering to ask someone else
        features['i_can_ask_person'] = low.str.contains(
            r'\bi\s+(?:can|could|will|ll)\s+ask\s+(?:her|him|them|my|the|around)',
            na=False
        ).astype(float)

        # Conditional/hypothetical — "if you want to ask", "when you ask"
        features['conditional_ask'] = low.str.contains(
            r'(?:if\s+(?:you|they|we|he|she)\s+.{0,20}ask'
            r'|when\s+(?:you|they)\s+ask)', na=False
        ).astype(float)

        # "Can I ask MY question in [channel]" — asking WHERE to post
        features['ask_my_question_in'] = low.str.contains(
            r'(?:can|could|should)\s+i\s+ask\s+(?:my|this|the)\s+question'
            r'\s+(?:in|on|at|there|here)', na=False
        ).astype(float)

        # "is it better to ask this in X" — asking WHERE
        features['better_to_ask_in'] = low.str.contains(
            r'(?:better|should\s+i)\s+(?:to\s+)?ask\s+(?:this|it|my)'
            r'\s+(?:in|on|at|there)', na=False
        ).astype(float)

        # Fictional/hypothetical context
        features['fictional_context'] = low.str.contains(
            r'(?:imagine\s+.{0,20}ask|in\s+the\s+game\s+.{0,20}ask'
            r'|the\s+character\s+.{0,10}ask'
            r'|if\s+you\s+could\s+ask\s+\w+\s+about'
            r'|when\s+\w+\s+can\s+ask\s+\w+\s+for)', na=False
        ).astype(float)

        # "which channel can I ask" — asking WHERE
        features['which_channel_ask'] = low.str.contains(
            r'which\s+(?:channel|server|room)\s+(?:can|should|do)\s+i\s+ask', na=False
        ).astype(float)

        # "can I move this question to X"
        features['move_question'] = low.str.contains(
            r'(?:can\s+i\s+)?move\s+(?:this|my)\s+question\s+to', na=False
        ).astype(float)

        # "should I post this in X" / "is X the right channel"
        features['routing_question'] = low.str.contains(
            r'(?:should\s+i\s+post\s+(?:this|my)|is\s+\w+\s+the\s+right\s+channel'
            r'|which\s+channel\s+(?:is\s+best|should))', na=False
        ).astype(float)

        # ---- POSITIVE SIGNALS (suggests IS asking-to-ask) ----

        # First-person "can/could/may/might I ask"
        features['first_person_can_i_ask'] = low.str.contains(
            r'\b(?:can|could|may|might)\s+i\s+ask\b', na=False
        ).astype(float)

        # "I'd like to ask" / "I would like to ask" / "I want to ask"
        features['id_like_to_ask'] = low.str.contains(
            r"(?:i'?d\s+like\s+to\s+ask|i\s+would\s+like\s+to\s+ask"
            r"|id\s+like\s+to\s+ask|i\s+wanna\s+ask|i\s+want\s+to\s+ask"
            r"|i\s+wanted\s+to\s+ask\s+(?:a\s+)?(?:question|something|for\s+help))",
            na=False
        ).astype(float)

        # Permission-seeking openers
        features['permission_seeking'] = low.str.contains(
            r'(?:mind\s+if\s+i'
            r'|would\s+it\s+be\s+(?:ok|okay|cool|alright|fine)'
            r'|is\s+it\s+(?:ok|okay|cool|alright|fine)\s+(?:if|to)\s+)',
            na=False
        ).astype(float)

        # "can I ask a question"
        features['can_i_ask_question'] = low.str.contains(
            r'(?:can|could|may)\s+i\s+ask\s+(?:a|some|another|one|my|any|two)'
            r'\s+(?:question|ques)', na=False
        ).astype(float)

        # "can I ask for help"
        features['can_i_ask_help'] = low.str.contains(
            r'(?:can|could|may)\s+i\s+ask\s+(?:for\s+)?help', na=False
        ).astype(float)

        # "can I ask ... here/in here/this server"
        features['ask_here'] = low.str.contains(
            r'(?:can|could|may)\s+i\s+ask\s+.{0,40}'
            r'(?:here|in\s+here|this\s+(?:server|channel))', na=False
        ).astype(float)

        # "is this the right place to ask"
        features['right_place_ask'] = low.str.contains(
            r'(?:is\s+this\s+.{0,20}(?:place|spot|channel)\s+.{0,10}ask'
            r'|acceptable\s+place\s+to\s+ask)', na=False
        ).astype(float)

        # Greeting + ask pattern
        features['greeting_then_ask'] = low.str.contains(
            r'^(?:hi|hey|hello|halo|helo|sup|yo|heya|hii|heyy)\b.{0,30}'
            r'\b(?:can|could|may|might)\s+i\s+ask', na=False
        ).astype(float)

        # Greeting + "I'd like to ask"
        features['greeting_then_id_like'] = low.str.contains(
            r"^(?:hi|hey|hello|halo|helo|sup|yo|heya|hii|heyy)\b.{0,30}"
            r"(?:i'?d\s+like|i\s+would\s+like|i\s+want)\s+to\s+ask",
            na=False
        ).astype(float)

        # "run something by you" / "bounce an idea" / "pick your brain"
        features['indirect_permission'] = low.str.contains(
            r'(?:run\s+(?:something|this|an?\s+\w+)\s+by\s+(?:you|ya|u)'
            r'|bounce\s+.{0,10}off|pick\s+your\s+brain'
            r'|shoot\s+you\s+a\s+question|throw\s+.{0,10}question'
            r'|let\s+me\s+run)', na=False
        ).astype(float)

        # "is it okay/cool to ask"
        features['is_it_ok_to_ask'] = low.str.contains(
            r'is\s+it\s+(?:ok|okay|cool|fine|alright|possible|permissible|acceptable)'
            r'\s+.{0,10}(?:to\s+ask|if\s+i\s+ask)', na=False
        ).astype(float)

        # "anyone I can ask"
        features['anyone_i_can_ask'] = low.str.contains(
            r'(?:anyone|anybody|someone|any\s+\w+)\s+.{0,15}'
            r'(?:i\s+can\s+ask|can\s+ask\s+(?:them|questions?|a\s+question))', na=False
        ).astype(float)

        # "run X by you"
        features['run_by_you'] = low.str.contains(
            r'(?:can\s+i\s+|let\s+me\s+|mind\s+if\s+i\s+)run\s+.{0,20}'
            r'by\s+(?:you|ya|u)', na=False
        ).astype(float)

        # ---- STRUCTURAL FEATURES ----

        features['word_count'] = low.str.split().str.len().fillna(0).astype(float)
        features['is_short'] = (features['word_count'] <= 10).astype(float)
        features['ends_question'] = low.str.strip().str.endswith('?').astype(float)

        features['has_question_word'] = low.str.contains(
            r'\b(?:question|ques|questions)\b', na=False
        ).astype(float)

        features['first_person'] = low.str.contains(
            r'\bi\s+(?:can|could|may|want|need|would|have)\b', na=False
        ).astype(float)

        features['question_about_asking'] = (
            features['ends_question']
            * low.str.contains(r'\bask\b', na=False).astype(float)
        )

        # ---- COMPOSITE DISAMBIGUATION ----

        # "can I ask" but NOT "where can I ask" and NOT "which channel"
        features['ask_not_where'] = (
            features['first_person_can_i_ask']
            * (1 - features['where_can_i_ask'])
            * (1 - features['which_channel_ask'])
        )

        # Has "ask" + third-person subject
        features['ask_third_person'] = (
            low.str.contains(r'\bask\b', na=False).astype(float)
            * features['third_person_ask']
        )

        # "can I ask" inside a meta/quoting context
        features['meta_can_i_ask'] = (
            features['first_person_can_i_ask']
            * features['meta_reference']
        )

        # Routing question composite
        features['ask_where_to_post'] = np.maximum(
            np.maximum(features['ask_my_question_in'], features['better_to_ask_in']),
            np.maximum(features['which_channel_ask'], features['move_question'])
        )

        # "I'd like / want to ask" but NOT routing
        features['like_to_ask_genuine'] = (
            features['id_like_to_ask']
            * (1 - features['ask_where_to_post'])
        )

        result = pd.DataFrame(features)
        return csr_matrix(result.fillna(0).values)