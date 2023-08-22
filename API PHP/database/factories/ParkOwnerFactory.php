<?php

namespace Database\Factories;

use App\Models\ParkOwner;
use Illuminate\Database\Eloquent\Factories\Factory;
use Illuminate\Support\Str;

class ParkOwnerFactory extends Factory
{
    /**
     * The name of the factory's corresponding model.
     *
     * @var string
     */
    protected $model = ParkOwner::class;

    /**
     * Define the model's default state.
     *
     * @return array
     */
    public function definition()
    {
        return [
            'name' => $this->faker->name(),
            'no_of_slots' => $this->faker->numberBetween(10, 100),
            'remain_slot' => $this->faker->numberBetween(10, 100),
            'price_per_hour' => $this->faker->numberBetween(1, 5),
            'contact' => $this->faker->phoneNumber,
            'park_lat' => $this->faker->numerify("##.##"),
            'park_lon' => $this->faker->numerify("##.##"),
            'email' => $this->faker->unique()->safeEmail(),
            'password' => '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', // password
        ];
    }
}
