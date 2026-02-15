# Claude generated
"""
Goosenet
Goosenet is an anti-spam and anti-scam bot. Skynet but for the goose servers.
"""

# IMPORTS
from sklearn.base import BaseEstimator, TransformerMixin
import pandas as pd
import numpy as np
from scipy.sparse import csr_matrix

class CustomFeatureExtractor(BaseEstimator, TransformerMixin):
	def fit(self, X, y=None):
		return self

	def transform(self, X):
		# Convert to Series if needed
		if isinstance(X, np.ndarray):
			X = pd.Series(X.flatten())
		elif not isinstance(X, pd.Series):
			X = pd.Series(X)

		features = pd.DataFrame()
		texts_lower = X.str.lower()

		# Location/channel indicators (suggests NOT "asking to ask")
		features['asks_where'] = texts_lower.str.contains(
			r'where (?:can|should|do|could|may) i', na=False
		).astype(int)
		features['mentions_location'] = texts_lower.str.contains(
			r'(?:channel|discord|server|place)', na=False
		).astype(int)

		# Fill NaN and return as sparse matrix for compatibility
		return csr_matrix(features.fillna(0).values)