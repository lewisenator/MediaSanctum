import { createContext, useContext, useState, useEffect } from 'react';
import { refresh } from "#/client/authClient.ts";
import { setStoredAccessToken } from "#/config/axios.ts";

type AuthContextType = {
  accessToken: string | null;
  setAccessToken: (accessToken: string | null) => void;
  user: {
    id: string;
    email: string;
    firstName: string;
    lastName: string;
  } | null;
  setUser: (user: AuthContextType['user']) => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({children}: {children: React.ReactNode}) {
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [user, setUser] = useState<AuthContextType['user'] | null>(null);

  useEffect(() => {
    const loadAuth = async () => {
      try {
        const { accessToken, user } = await refresh();
        setAccessToken(accessToken);
        setStoredAccessToken(accessToken);
        setUser(user);
      } catch (err: any) {
        console.error('Failed to refresh access token', (err?.message || 'Unknown Error'));
      }
    };
    loadAuth();
  }, []);

  useEffect(() => {
    setStoredAccessToken(accessToken);
  }, [accessToken]);

  return (
    <AuthContext.Provider value={{
      accessToken,
      setAccessToken,
      user,
      setUser
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};