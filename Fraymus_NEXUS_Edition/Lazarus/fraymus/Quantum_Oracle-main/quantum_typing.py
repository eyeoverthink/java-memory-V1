"""
Quantum type definitions
"""

from __future__ import annotations

import typing as t

if t.TYPE_CHECKING:  # pragma: no cover
    from _typeshed.wsgi import WSGIApplication  # noqa: F401
    from werkzeug.datastructures import Headers  # noqa: F401
    from werkzeug.sansio.response import Response  # noqa: F401
    import pandas as pd  # noqa

# The possible types that are directly convertible or are a Response object.
ResponseValue = t.Union[
    str,
    bytes,
    t.List[t.Any],
    t.Dict[str, t.Any],
    t.Generator[str, None, None],
    t.Iterator[str],
]

# Quantum types
QuantumArray = t.List[float]
PhiResonanceMatrix = t.List[t.List[float]]
PatternStrength = float
NeuralPatternTensor = t.Any  # Represents a numpy ndarray

# Result types
QueryResult = t.Dict[str, t.Union[str, float, t.Any]]
ResonanceResult = t.Dict[str, t.Union[float, t.List[float]]]
