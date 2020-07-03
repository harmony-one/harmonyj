pragma solidity >=0.4.21 <0.6.0;

contract Counter {
    int256 private count = 0;

    function incrementCounter() public {
        count += 1;
    }

    function decrementCounter() public {
        count -= 1;
    }

    function getCount() public view returns (int256) {
        return count;
    }
}
