import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilterResolutionComponent } from './filter-resolution.component';

describe('FilterResolutionComponent', () => {
  let component: FilterResolutionComponent;
  let fixture: ComponentFixture<FilterResolutionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FilterResolutionComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(FilterResolutionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
