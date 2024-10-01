// class House 
//     def House (int rooms, int bathrooms, string ownerName, float marketPrice):
//         #default values
//         rooms = 1 
//         bathrooms = 1
//         ownerName = "John"
//         marketPrice = 1.0
#include <string>
class House {
    public:
        House(int fans, int lights, int doors, std::string roomOwner, float rent) : fans(fans), lights(lights), doors(doors), roomOwner(roomOwner), rent(rent) {}
        void setFans(int fans) {
            this->fans = fans;
        }
        void setLights(int lights) {
            this->lights = lights;
        }
        void setDoors(int doors) {
            this->doors = doors;
        }
        void setRoomOwner(std::string roomOwner) {
            this->roomOwner = roomOwner;
        }
        void setRent(float rent) {
            this->rent = rent;
        }
    private:
        int fans;
        int lights;
        int doors;
        std::string roomOwner;
        float rent;
};

class Bedroom : public House {
    public:
        Bedroom(int fans, int lights, int doors, std::string roomOwner, float rent, int beds, int windows) : House(fans, lights, doors, roomOwner, rent), beds(beds), windows(windows) {}
        void setBeds(int beds) {
            this->beds = beds;
        }
        void setWindows(int windows) {
            this->windows = windows;
        }
    private:
        int beds;
        int windows;
};

class Kitchen : public House {
    public:
        Kitchen(int fans, int lights, int doors, std::string roomOwner, float rent, int stoves, int fridges) : House(fans, lights, doors, roomOwner, rent), stoves(stoves), fridges(fridges) {}
        void setStoves(int stoves) {
            this->stoves = stoves;
        }
        void setFridges(int fridges) {
            this->fridges = fridges;
        }
    private:
        int stoves;
        int fridges;
};

class Bathroom : House {
    public:
        Bathroom(int fans, int lights, int doors, std::string roomOwner, float rent, int showers, int sinks) : House(fans, lights, doors, roomOwner, rent), showers(showers), sinks(sinks) {}
        void setShowers(int showers) {
            this->showers = showers;
        }
        void setSinks(int sinks) {
            this->sinks = sinks;
        }
    private:
        int showers;
        int sinks;
};