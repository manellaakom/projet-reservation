import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { BagageService } from './bagage.service';
import { Bagage, BagageStatistics } from '../models/bagage.model';

describe('BagageService', () => {
  let service: BagageService;
  let httpMock: HttpTestingController;
  const baseUrl = 'http://localhost:8080/api/bagages';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BagageService]
    });
    service = TestBed.inject(BagageService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAllBagages() should fetch bagages from Gateway', () => {
    const dummyBagages: Bagage[] = [
      { id: '1', reservationId: 'RES-1', type: 'CABINE', poids: 8, statut: 'REGISTERED', prix: 30 }
    ];

    service.getAllBagages().subscribe(bagages => {
      expect(bagages.length).toBe(1);
      expect(bagages[0].id).toBe('1');
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush(dummyBagages);
  });

  it('associateToReservation() should perform a PATCH request', () => {
    const dummyUpdated: Bagage = {
      id: 'BAG-100',
      reservationId: 'RES-NEW-99',
      type: 'CABINE',
      poids: 9.5,
      statut: 'REGISTERED',
      prix: 30
    };

    service.associateToReservation('BAG-100', 'RES-NEW-99').subscribe(res => {
      expect(res.reservationId).toBe('RES-NEW-99');
    });

    const req = httpMock.expectOne(`${baseUrl}/BAG-100/reservation/RES-NEW-99`);
    expect(req.request.method).toBe('PATCH');
    req.flush(dummyUpdated);
  });

  it('getStatistics() should fetch statistics from Gateway', () => {
    const dummyStats: BagageStatistics = {
      totalBagages: 4,
      totalWeight: 78,
      totalRevenue: 220,
      averageWeight: 19.5,
      pendingBagages: 1,
      registeredBagages: 3
    };

    service.getStatistics().subscribe(stats => {
      expect(stats.totalBagages).toBe(4);
      expect(stats.totalRevenue).toBe(220);
    });

    const req = httpMock.expectOne(`${baseUrl}/statistics`);
    expect(req.request.method).toBe('GET');
    req.flush(dummyStats);
  });

  describe('calculateEstimatedPrice()', () => {
    it('should return 30 DT for weights between 0 and 10 kg', () => {
      expect(service.calculateEstimatedPrice(5)).toBe(30.0);
      expect(service.calculateEstimatedPrice(10)).toBe(30.0);
    });

    it('should return 50 DT for weights between 10.1 and 20 kg', () => {
      expect(service.calculateEstimatedPrice(12)).toBe(50.0);
      expect(service.calculateEstimatedPrice(20)).toBe(50.0);
    });

    it('should return 70 DT for weights between 20.1 and 30 kg', () => {
      expect(service.calculateEstimatedPrice(22)).toBe(70.0);
      expect(service.calculateEstimatedPrice(30)).toBe(70.0);
    });

    it('should return null for invalid weights (> 30 kg, 0 or negative)', () => {
      expect(service.calculateEstimatedPrice(35)).toBeNull();
      expect(service.calculateEstimatedPrice(0)).toBeNull();
      expect(service.calculateEstimatedPrice(-5)).toBeNull();
      expect(service.calculateEstimatedPrice(null)).toBeNull();
    });
  });
});
