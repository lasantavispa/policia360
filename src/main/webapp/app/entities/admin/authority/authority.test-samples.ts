import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '93e315b5-034b-48fd-bd38-42593b8edc92',
};

export const sampleWithPartialData: IAuthority = {
  name: 'f644a25d-3277-4441-a092-8c44139374e2',
};

export const sampleWithFullData: IAuthority = {
  name: '8d98be47-af7e-4074-ba46-c501301b3552',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
