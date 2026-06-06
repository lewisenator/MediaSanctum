import api from "#/config/axios.ts";

export type HealthResponse = {
  groups: string[];
  status: string;
};

export const health = async (): Promise<HealthResponse> => {
  const res = await api.get('/actuator/health');
  return res.data;
};