import { Owner } from './owner';
import { Sex } from './sex';

export interface Horse {
  id?: number;
  name: string;
  description?: string;
  dateOfBirth: Date | string;
  sex: Sex;
  owner?: Owner;
  femaleParentId?: number;
  maleParentId?: number;
}

export interface PersistedHorse extends Horse {
  id: number;
}

export interface CreateUpdateHorseDto {
  id?: number;
  name: string;
  description?: string;
  dateOfBirth: Date | string;
  sex: Sex;
  owner?: Owner;
  femaleParent: Horse | undefined | null;
  maleParent: Horse | undefined | null;
}

export interface HorseCreateDto extends CreateUpdateHorseDto {
  ownerId?: number;
  femaleParentId?: number;
  maleParentId?: number;
}

export interface HorseSearch {
  name?: string;
  description?: string;
  bornBefore?: Date | string;
  bornAfter?: Date | string;
  sex?: Sex;
  ownerName?: string;
  excludeHorseById?: number;
  limit?: number;
}

export interface HorseAncestrySearch {
  id: string;
  limit?: number;
}
