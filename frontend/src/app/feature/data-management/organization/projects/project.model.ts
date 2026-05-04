export type ProjectRegion = 'US' | 'EMEA' | 'AU';
export type ProjectStatus = 'ACTIVE' | 'ON_HOLD' | 'COMPLETED' | 'CANCELLED' | 'TO_BE_STARTED';

export interface Project {
  id: number | null;
  name: string;
  code: string;
  region: ProjectRegion;
  status: ProjectStatus;
  startDate: string;
  endDate: string | null;
  psFee?: number | null;
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
  psFee?: number | null;
}
