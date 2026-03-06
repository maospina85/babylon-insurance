import { useState, useEffect } from 'react';
import { fetchCatalog } from '../api/productApi';

/**
 * Fetches the active product catalogue on mount.
 * @returns {{ modules: Array, loading: boolean, error: string|null }}
 */
export function useProduct() {
  const [modules, setModules] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchCatalog()
      .then((data) => setModules(data.modules))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  return { modules, loading, error };
}
