import { Test, TestingModule } from '@nestjs/testing';
import { AnalysisController } from './analysis.controller';
import { AnalysisService } from './analysis.service';
import { HttpModule } from '@nestjs/axios';

describe('AnalysisController', () => {
  let controller: AnalysisController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [HttpModule],
      controllers: [AnalysisController],
      providers: [AnalysisService],
    }).compile();

    controller = module.get<AnalysisController>(AnalysisController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
