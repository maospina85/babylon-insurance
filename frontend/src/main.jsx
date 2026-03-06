import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { ThemeContext } from './context/ThemeContext';
import { theme } from './styles/theme';
import App from './App';

// Inject Google Font declared in theme
const link = document.createElement('link');
link.rel = 'stylesheet';
link.href = theme.typography.fontUrl;
document.head.appendChild(link);

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <ThemeContext.Provider value={theme}>
      <App />
    </ThemeContext.Provider>
  </StrictMode>,
);
