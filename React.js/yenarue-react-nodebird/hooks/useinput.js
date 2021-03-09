import { useState, useCallback } from 'react';

export default (initialValue = null) => {
  const [value, setValue] = useState(initialValue);

  // 함수파라미터로 넘길때... 최적화를 위한 useCallback
  const handler = useCallback((e) => {
    setValue(e.target.value);
  }, []);

  return [value, handler];
}