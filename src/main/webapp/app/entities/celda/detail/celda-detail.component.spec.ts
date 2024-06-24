import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CeldaDetailComponent } from './celda-detail.component';

describe('Celda Management Detail Component', () => {
  let comp: CeldaDetailComponent;
  let fixture: ComponentFixture<CeldaDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CeldaDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: CeldaDetailComponent,
              resolve: { celda: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CeldaDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CeldaDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load celda on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CeldaDetailComponent);

      // THEN
      expect(instance.celda()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
