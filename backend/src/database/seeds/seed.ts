import dataSource from '../../../typeorm.data-source';
import { SeedLoader } from './seed-loader';
import { SeedRunner } from './seed-runner';

async function bootstrap() {
  await dataSource.initialize();
  const seedRunner = new SeedRunner(dataSource, SeedLoader.load());
  await seedRunner.runAll();
  await dataSource.destroy();
}

bootstrap().catch((error) => {
  console.error('Error during seeding:', error);
  process.exit(1);
});
