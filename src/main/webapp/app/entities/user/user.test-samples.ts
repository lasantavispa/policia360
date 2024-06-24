import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 26987,
  login: 'r',
};

export const sampleWithPartialData: IUser = {
  id: 28584,
  login: 'cY',
};

export const sampleWithFullData: IUser = {
  id: 31316,
  login: 'hgGO1+@l\\+RcB\\jHpjg7\\gTE\\IGvnqJ',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
