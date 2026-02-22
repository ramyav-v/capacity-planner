export type ProjectRegion = 'US' | 'EMEA' | 'AU';
export type ProjectStatus = 'ACTIVE' | 'ON_HOLD' | 'COMPLETED' | 'CANCELLED';

export interface Project {
  id: number | null;
  name: string;
  code: string;
  region: ProjectRegion;
  status: ProjectStatus;
  startDate: string;
  endDate: string | null;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface ProjectDto {
  name: string;
  code: string;
  region: ProjectRegion;
  status: ProjectStatus;
  startDate: string;
  endDate: string | null;
}
