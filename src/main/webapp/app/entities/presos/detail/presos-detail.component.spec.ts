import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PresosDetailComponent } from './presos-detail.component';

describe('Presos Management Detail Component', () => {
  let comp: PresosDetailComponent;
  let fixture: ComponentFixture<PresosDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PresosDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: PresosDetailComponent,
              resolve: { presos: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PresosDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PresosDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load presos on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PresosDetailComponent);

      // THEN
      expect(instance.presos()).toEqual(expect.objectContaining({ id: 123 }));
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
