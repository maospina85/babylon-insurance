/**
 * @typedef {typeof theme} Theme
 *
 * ÚNICO archivo que cambia por cliente.
 * Cambiar este objeto adapta toda la UI sin tocar ningún componente.
 */
export const theme = {
  brand: {
    name: 'Babylon',
    tagline: 'Tu seguro a medida',
    logo: null, // ReactComponent | null → usa texto estilizado
    gradient: 'linear-gradient(135deg, #2B5BF5 0%, #7B2FF7 50%, #E91E8C 100%)',
    gradientSoft: 'linear-gradient(135deg, #EEF2FF 0%, #F5F0FF 50%, #FFF0F7 100%)',
    primary: '#2B5BF5',
    secondary: '#E91E8C',
    bg: '#F0F4FF',
    surface: '#FFFFFF',
    muted: '#6B7280',
    textPrimary: '#111827',
    textSecondary: '#374151',
    success: '#059669',
    warning: '#D97706',
    error: '#DC2626',
  },
  modules: {
    death: {
      bg: '#EEF2FF',
      light: '#E0E7FF',
      accent: '#2B5BF5',
      text: '#1E40AF',
      ring: '#93C5FD',
      icon: '🛡️',
      label: 'Vida / Fallecimiento',
    },
    disability: {
      bg: '#FDF4FF',
      light: '#F3E8FF',
      accent: '#7B2FF7',
      text: '#6B21A8',
      ring: '#C4B5FD',
      icon: '⚕️',
      label: 'Invalidez Total',
    },
    accidents: {
      bg: '#F0FDF4',
      light: '#DCFCE7',
      accent: '#059669',
      text: '#065F46',
      ring: '#6EE7B7',
      icon: '⚡',
      label: 'Accidentes',
    },
    assistance: {
      bg: '#FFF7ED',
      light: '#FED7AA',
      accent: '#EA580C',
      text: '#9A3412',
      ring: '#FCA5A5',
      icon: '🌐',
      label: 'Asistencias',
    },
  },
  typography: {
    fontFamily: "'DM Sans', sans-serif",
    fontUrl:
      'https://fonts.googleapis.com/css2?family=DM+Sans:ital,opsz,wght@0,9..40,400;0,9..40,500;0,9..40,700;0,9..40,800;0,9..40,900&display=swap',
  },
  shape: {
    cardRadius: '24px',
    moduleRadius: '24px',
    buttonRadius: '16px',
    inputRadius: '14px',
    chipRadius: '999px',
    tierRadius: '16px',
  },
  shadow: {
    card: '0 2px 8px rgba(0,0,0,0.06)',
    active: (ring) => `0 8px 32px ${ring}44`,
    button: '0 4px 16px rgba(43,91,245,0.4)',
    elevated: '0 8px 32px rgba(43,91,245,0.15)',
  },
  animation: {
    expand: 'max-height 0.4s cubic-bezier(0.4,0,0.2,1), opacity 0.3s ease',
    toggle: 'transform 0.3s cubic-bezier(0.4,0,0.2,1)',
    card: 'transform 0.2s ease, box-shadow 0.2s ease',
  },
};
